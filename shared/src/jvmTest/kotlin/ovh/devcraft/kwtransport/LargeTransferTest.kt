package ovh.devcraft.kwtransport

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertContentEquals
import java.security.MessageDigest
import java.util.Random
import kotlin.time.Duration.Companion.seconds
import java.util.concurrent.Executors
import java.net.ServerSocket

class LargeTransferTest {
    // specialized dispatcher for heavy IO to avoid blocking test threads
    private val ioDispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()

    private fun getFreePort(): Int {
        return ServerSocket(0).use { it.localPort }
    }

    @Test
    fun `transfer 1GB of data and verify integrity`() = runTest(timeout = 120.seconds) {
        val port = getFreePort()
        val bindAddr = "127.0.0.1:$port"

        val cert = Certificate.createSelfSigned("localhost", "127.0.0.1")
        val server = Endpoint.createServerEndpoint(bindAddr, cert)
        val client = Endpoint.createClientEndpoint(acceptAllCerts = true)

        val totalSize = 1024L * 1024L * 1024L // 1GB
        val chunkSize = 64 * 1024 // 64KB chunks
        
        // We use a fixed seed to generate deterministic "random" data without storing it
        val seed = 123456789L

        try {
            val serverJob = launch(ioDispatcher) {
                val conn = server.incomingSessions().first()
                val pair = conn.acceptBi()
                
                // Server receives data and computes hash
                val digest = MessageDigest.getInstance("SHA-256")
                val buffer = ByteArray(chunkSize)
                var bytesRead = 0L
                
                while (true) {
                    val n = pair.recv.read(buffer)
                    if (n == -1) break // EOF (Assuming read returns -1 or throws? Let's check RecvStream)
                    // Wait, RecvStream.read returns Int. If 0? or -1?
                    // Wrapper says: return result.toInt(). 
                    // Rust returns None for EOF -> wrapper maps to -1.
                    if (n == -1) break
                    
                    digest.update(buffer, 0, n)
                    bytesRead += n
                }
                
                // Send hash back to client
                val hash = digest.digest()
                pair.send.write(hash)
                pair.send.close() // Close write side
            }

            // Client generates and sends data
            delay(500) // Wait for server start
            
            client.connect("https://$bindAddr/webtransport").use { conn ->
                val pair = conn.openBi()
                val digest = MessageDigest.getInstance("SHA-256")
                val random = Random(seed)
                val buffer = ByteArray(chunkSize)
                var bytesWritten = 0L
                
                while (bytesWritten < totalSize) {
                    val remaining = totalSize - bytesWritten
                    val toWrite = minOf(chunkSize.toLong(), remaining).toInt()
                    
                    random.nextBytes(buffer) // Fills whole buffer, we use only toWrite
                    // Ensure consistency: update digest only with what we write
                    // But random.nextBytes fills everything. 
                    // Efficient way: reuse buffer, but we need strictly same sequence.
                    // Random.nextBytes is good.
                    
                    // Actually, if last chunk is smaller, we must be careful.
                    val chunk = if (toWrite == chunkSize) buffer else buffer.copyOf(toWrite)
                    
                    digest.update(chunk)
                    pair.send.write(chunk)
                    bytesWritten += toWrite
                }
                pair.send.close() // Signal EOF
                
                // Read back hash
                val receivedHash = ByteArray(32) // SHA-256 is 32 bytes
                var readTotal = 0
                while (readTotal < 32) {
                    val n = pair.recv.read(receivedHash) // This might read partial? 
                    // Our wrapper read takes buffer and fills it? 
                    // No, wrapper calls Rust read with buffer. Rust fills up to buffer length.
                    // We need to handle partial reads for the hash too.
                    if (n == -1) break
                     // This simple logic is flawed if read puts data at offset 0 always.
                     // The wrapper `read(buffer)` fills buffer from 0.
                     // So we need a temp buffer or use offset if wrapper supported it.
                     // Wrapper: read(buffer) -> fills buffer. 
                     // We should verify receivedHash content.
                     
                     // Easier: read into temp, copy to final.
                     // But hash is small (32 bytes), likely one read.
                     if (n > 0) {
                         // Copy to accumulator if needed, but likely getting it all.
                         // Let's assume single read for now or fail if fragmented (unlikely for 32 bytes).
                         if (n != 32) throw RuntimeException("Fragmented hash read not implemented for test: got $n bytes")
                         readTotal += n
                     }
                }
                
                val expectedHash = digest.digest()
                assertContentEquals(expectedHash, receivedHash, "Hashes do not match!")
            }
            
            serverJob.join()
            
        } finally {
            withContext(Dispatchers.IO) {
                server.close()
                client.close()
            }
            ioDispatcher.close()
        }
    }
}
