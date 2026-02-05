package ovh.devcraft.kwtransport

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals

class IntegrationTest {

    @Test
    fun `should perform full bidirectional round trip`() = runTest {
        println("[Test] Starting integration test...")
        val serverAddr = "127.0.0.1:4433"
        
        println("[Test] Creating self-signed certificate...")
        val cert = withContext(Dispatchers.IO) {
            Certificate.createSelfSigned("localhost", "127.0.0.1")
        }
        
        println("[Test] Creating server endpoint...")
        val serverEndpoint = withContext(Dispatchers.IO) {
            Endpoint.createServerEndpoint(serverAddr, cert)
        }
        
        println("[Test] Creating client endpoint...")
        val clientEndpoint = withContext(Dispatchers.IO) {
            Endpoint.createClientEndpoint(acceptAllCerts = true)
        }

        try {
            println("[Test] Launching server job...")
            val serverJob = launch(Dispatchers.IO) {
                println("[Server] Waiting for session...")
                serverEndpoint.acceptSession().use { conn ->
                    println("[Server] Session accepted. Waiting for bi-stream...")
                    val pair = conn.acceptBi()
                    println("[Server] Bi-stream accepted. Reading data...")
                    val buffer = ByteArray(1024)
                    val n = pair.recv.read(buffer)
                    println("[Server] Read $n bytes. Echoing back...")
                    if (n > 0) {
                        val received = buffer.copyOf(n)
                        pair.send.write(received)
                        println("[Server] Data echoed.")
                    }
                }
            }

            println("[Test] Waiting 200ms for server to stabilize...")
            delay(200)

            println("[Test] Client connecting to $serverAddr...")
            withContext(Dispatchers.IO) {
                clientEndpoint.connect("https://$serverAddr/webtransport").use { conn ->
                    println("[Client] Connected. Opening bi-stream...")
                    val pair = conn.openBi()
                    println("[Client] Bi-stream opened. Sending 'Hello World'...")
                    val data = "Hello World".toByteArray()
                    pair.send.write(data)
                    
                    println("[Client] Data sent. Reading echo...")
                    val buffer = ByteArray(1024)
                    val n = pair.recv.read(buffer)
                    val received = buffer.copyOf(n)
                    println("[Client] Read $n bytes: ${String(received)}")
                    
                    assertContentEquals(data, received)
                }
            }

            println("[Test] Waiting for server job to finish...")
            serverJob.join()
            println("[Test] Server job finished.")
        } finally {
            println("[Test] Closing endpoints...")
            withContext(Dispatchers.IO) {
                serverEndpoint.close()
                clientEndpoint.close()
            }
            println("[Test] Endpoints closed.")
        }
    }
}
