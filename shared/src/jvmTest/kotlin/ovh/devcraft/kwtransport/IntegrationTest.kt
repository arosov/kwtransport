package ovh.devcraft.kwtransport

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.milliseconds
import java.net.ServerSocket

class IntegrationTest {
    private val stressDispatcher = Executors.newFixedThreadPool(64).asCoroutineDispatcher()

    @Test
    fun `should perform full bidirectional round trip`() = runTest {
        val cert = Certificate.createSelfSigned("localhost", "127.0.0.1")
        val serverEndpoint = Endpoint.createServerEndpoint("127.0.0.1:0", cert)
        val serverAddr = serverEndpoint.localAddr
        val clientEndpoint = Endpoint.createClientEndpoint(acceptAllCerts = true)

        try {
            val serverJob = launch(Dispatchers.IO) {
                val conn = serverEndpoint.incomingSessions().first()
                conn.use {
                    val pair = conn.acceptBi()
                    val buffer = ByteArray(1024)
                    val n = pair.recv.read(buffer)
                    if (n > 0) {
                        pair.send.write(buffer.copyOf(n))
                    }
                }
            }

            clientEndpoint.connect("https://$serverAddr/webtransport").use { conn ->
                val pair = conn.openBi()
                val data = "Hello World".toByteArray()
                pair.send.write(data)
                
                val buffer = ByteArray(1024)
                val n = pair.recv.read(buffer)
                assertContentEquals(data, buffer.copyOf(n))
            }

            serverJob.join()
        } finally {
            withContext(Dispatchers.IO) {
                serverEndpoint.close()
                clientEndpoint.close()
            }
        }
    }

    @Test
    fun `stress test with many concurrent streams`() = runTest(timeout = 60000L.milliseconds) {
        val concurrentStreams = 30
        val messagesPerStream = 5
        val completedStreams = AtomicInteger(0)

        val cert = Certificate.createSelfSigned("localhost", "127.0.0.1")
        val serverEndpoint = Endpoint.createServerEndpoint("127.0.0.1:0", cert)
        val serverAddr = serverEndpoint.localAddr
        val clientEndpoint = Endpoint.createClientEndpoint(acceptAllCerts = true)

        try {
            val serverJob = launch(stressDispatcher) {
                val conn = serverEndpoint.incomingSessions().first()
                conn.use {
                    val jobs = (1..concurrentStreams).map {
                        launch(stressDispatcher) {
                            val pair = conn.acceptBi()
                            repeat(messagesPerStream) {
                                val buffer = ByteArray(1024)
                                val n = pair.recv.read(buffer)
                                if (n > 0) {
                                    pair.send.write(buffer.copyOf(n))
                                }
                            }
                        }
                    }
                    jobs.joinAll()
                }
            }

            clientEndpoint.connect("https://$serverAddr/webtransport").use { conn ->
                val streamJobs = (1..concurrentStreams).map { i ->
                    launch(stressDispatcher) {
                        val pair = conn.openBi()
                        repeat(messagesPerStream) { m ->
                            val data = "Stream $i Message $m".toByteArray()
                            pair.send.write(data)
                            val buffer = ByteArray(1024)
                            val n = pair.recv.read(buffer)
                            assertContentEquals(data, buffer.copyOf(n))
                        }
                        completedStreams.incrementAndGet()
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
            val port = getFreePort()
            val serverAddr = "127.0.0.1:$port"
            
            val cert = Certificate.createSelfSigned("localhost")
            val server = Endpoint.createServerEndpoint(serverAddr, cert)
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
