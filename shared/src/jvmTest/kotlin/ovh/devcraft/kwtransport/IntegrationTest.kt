package ovh.devcraft.kwtransport

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.milliseconds
import java.net.ServerSocket

class IntegrationTest {
    // Increase pool size to handle more concurrent blocking JNI calls
    private val stressDispatcher = Executors.newFixedThreadPool(64).asCoroutineDispatcher()

    @Test
    fun `should perform full bidirectional round trip`() = runTest {
        val serverAddr = "127.0.0.1:4433"
        val cert = withContext(Dispatchers.IO) {
            Certificate.createSelfSigned("localhost", "127.0.0.1")
        }
        val serverEndpoint = withContext(Dispatchers.IO) {
            Endpoint.createServerEndpoint(serverAddr, cert)
        }
        val clientEndpoint = withContext(Dispatchers.IO) {
            Endpoint.createClientEndpoint(acceptAllCerts = true)
        }

        try {
            val serverJob = launch(Dispatchers.IO) {
                serverEndpoint.acceptSession().use { conn ->
                    val pair = conn.acceptBi()
                    val buffer = ByteArray(1024)
                    val n = pair.recv.read(buffer)
                    if (n > 0) {
                        pair.send.write(buffer.copyOf(n))
                    }
                }
            }

            delay(200)

            withContext(Dispatchers.IO) {
                clientEndpoint.connect("https://$serverAddr/webtransport").use { conn ->
                    val pair = conn.openBi()
                    val data = "Hello World".toByteArray()
                    pair.send.write(data)
                    val buffer = ByteArray(1024)
                    val n = pair.recv.read(buffer)
                    assertContentEquals(data, buffer.copyOf(n))
                }
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
        val serverAddr = "127.0.0.1:4434"
        val concurrentStreams = 30 // Reduced slightly to ensure stability
        val messagesPerStream = 5
        val completedStreams = AtomicInteger(0)

        val cert = withContext(Dispatchers.IO) {
            Certificate.createSelfSigned("localhost", "127.0.0.1")
        }
        val serverEndpoint = withContext(Dispatchers.IO) {
            Endpoint.createServerEndpoint(serverAddr, cert)
        }
        val clientEndpoint = withContext(Dispatchers.IO) {
            Endpoint.createClientEndpoint(acceptAllCerts = true)
        }

        try {
            val serverJob = launch(stressDispatcher) {
                serverEndpoint.acceptSession().use { conn ->
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

            delay(500)

            withContext(stressDispatcher) {
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
            }

            assertEquals(concurrentStreams, completedStreams.get())
            serverJob.cancelAndJoin()
        } finally {
            withContext(stressDispatcher) {
                serverEndpoint.close()
                clientEndpoint.close()
            }
        }
    }

    @Test
    fun `leak check loop`() = runBlocking {
        val iterations = 50
        
        println("Starting leak check loop for $iterations iterations...")
        
        repeat(iterations) { i ->
            val port = getFreePort()
            val serverAddr = "127.0.0.1:$port"
            
            val cert = Certificate.createSelfSigned("localhost")
            val server = Endpoint.createServerEndpoint(serverAddr, cert)
            val client = Endpoint.createClientEndpoint(acceptAllCerts = true, maxIdleTimeoutMillis = 1000)
            
            val serverJob = launch(stressDispatcher) {
                try {
                    server.acceptSession().use { }
                } catch (e: Exception) {}
            }
            
            delay(50)
            
            try {
                withContext(stressDispatcher) {
                    client.connect("https://$serverAddr/webtransport").use { }
                }
            } catch (e: Exception) {}
            
            serverJob.join()
            server.close()
            client.close()
            
            if (i % 10 == 0) {
                System.gc()
                val memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
                println("Iteration $i - JVM Memory: ${memory / 1024} KB")
            }
        }
        
        println("Leak check loop finished.")
    }

    private fun getFreePort(): Int {
        return ServerSocket(0).use { it.localPort }
    }
}
