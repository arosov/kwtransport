package io.github.arosov.kwtransport

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.net.ServerSocket
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals

class IntegrationTest {

    private val stressDispatcher = Dispatchers.IO.limitedParallelism(16)

    @Test
    fun `should perform full bidirectional round trip`() = runBlocking {
        val cert = Certificate.createSelfSigned("localhost", "127.0.0.1")
        val hash = cert.getHash()

        val serverEndpoint = Endpoint.createServerEndpoint("127.0.0.1:0", cert)
        val serverAddr = serverEndpoint.localAddr
        val url = "https://$serverAddr/test"

        val message = "Hello WebTransport"
        
        val serverJob = launch(stressDispatcher) {
            serverEndpoint.incomingSessions().collect { connection ->
                val pair = connection.acceptBi()
                val buffer = ByteArray(1024)
                val n = pair.recv.read(buffer)
                val received = buffer.decodeToString(0, n)
                pair.send.write(received)
                pair.send.close()
                connection.close()
            }
        }

        val clientEndpoint = Endpoint.createClientEndpoint(
            certificateHashes = listOf(hash)
        )

        val connection = clientEndpoint.connect(url)
        val pair = connection.openBi()
        pair.send.write(message)
        
        val buffer = ByteArray(1024)
        val n = pair.recv.read(buffer)
        val response = buffer.decodeToString(0, n)

        assertEquals(message, response)

        connection.close()
        serverJob.cancelAndJoin()
        serverEndpoint.close()
        clientEndpoint.close()
    }

    @Test
    fun `stress test with many concurrent streams`() = runBlocking {
        val concurrentStreams = 100
        val cert = Certificate.createSelfSigned("localhost", "127.0.0.1")
        val hash = cert.getHash()

        val quicConfig = QuicConfig(
            maxConcurrentBiStreams = 200,
            initialMaxStreamDataBidiLocal = 1024 * 1024,
            initialMaxStreamDataBidiRemote = 1024 * 1024
        )

        val serverEndpoint = Endpoint.createServerEndpoint(
            bindAddr = "127.0.0.1:0", 
            certificate = cert,
            quicConfig = quicConfig
        )
        val serverAddr = serverEndpoint.localAddr
        val url = "https://$serverAddr/stress"

        val completedStreams = AtomicInteger(0)

        val serverJob = launch(stressDispatcher) {
            serverEndpoint.incomingSessions().collect { connection ->
                launch(stressDispatcher) {
                    repeat(concurrentStreams) {
                        launch(stressDispatcher) {
                            try {
                                val pair = connection.acceptBi()
                                val buffer = ByteArray(1024)
                                val n = pair.recv.read(buffer)
                                if (n > 0) {
                                    pair.send.write(buffer.copyOf(n))
                                }
                                pair.send.close()
                            } catch (e: Exception) {}
                        }
                    }
                }
            }
        }

        val clientEndpoint = Endpoint.createClientEndpoint(
            certificateHashes = listOf(hash),
            quicConfig = quicConfig
        )

        try {
            val connection = clientEndpoint.connect(url)
            
            coroutineScope {
                val streamJobs = (1..concurrentStreams).map { i ->
                    launch(stressDispatcher) {
                        try {
                            val pair = connection.openBi()
                            val data = "Message $i".encodeToByteArray()
                            pair.send.write(data)
                            
                            val buffer = ByteArray(1024)
                            val n = pair.recv.read(buffer)
                            if (n == data.size) {
                                completedStreams.incrementAndGet()
                            }
                            pair.send.close()
                        } catch (e: Exception) {
                            println("Stream $i failed: ${e.message}")
                        }
                    }
                }
                streamJobs.joinAll()
            }

            assertEquals(concurrentStreams, completedStreams.get())
            serverJob.cancelAndJoin()
        } finally {
            withContext(Dispatchers.IO) {
                serverEndpoint.close()
                clientEndpoint.close()
            }
        }
    }

    @Test
    fun `leak check loop`() = runBlocking {
        val iterations = 50
        repeat(iterations) { i ->
            val cert = Certificate.createSelfSigned("localhost")
            val server = Endpoint.createServerEndpoint("127.0.0.1:0", cert)
            val serverAddr = server.localAddr
            val client = Endpoint.createClientEndpoint(acceptAllCerts = true, maxIdleTimeoutMillis = 1000)
            
            val serverJob = launch(stressDispatcher) {
                try {
                    val conn = server.incomingSessions().first()
                    conn.close()
                } catch (e: Exception) {}
            }
            
            delay(50)
            
            try {
                client.connect("https://$serverAddr/webtransport").use { }
            } catch (e: Exception) {}
            
            serverJob.join()
            server.close()
            client.close()
            
            if (i % 10 == 0) {
                System.gc()
            }
        }
    }

    private fun getFreePort(): Int {
        return ServerSocket(0).use { it.localPort }
    }
}
