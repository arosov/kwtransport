package io.github.arosov.kwtransport

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class CancellationTest {

    @Test
    fun `cancel connect should finish job`() = runTest(timeout = 10.seconds) {
        val client = Endpoint.createClientEndpoint()
        try {
            // Launch a connect job that we expect to hang (using a blackhole IP or just non-existent)
            // 192.0.2.1 is TEST-NET-1, usually unreachable.
            val job = launch {
                try {
                    client.connect("https://192.0.2.1/webtransport")
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    // Ignore network errors, we just want to ensure we can cancel the wait
                }
            }
            delay(100)
            // If it already failed, fine. If running, we cancel.
            if (job.isActive) {
                job.cancelAndJoin()
            }
            assertTrue(job.isCancelled || job.isCompleted)
        } finally {
            client.close()
        }
    }

    @Test
    fun `cancel mid-stream write should finish job`() = runTest(timeout = 10.seconds) {
        val port = TestUtils.getFreePort()
        val bindAddr = "127.0.0.1:$port"
        val cert = Certificate.createSelfSigned("localhost", "127.0.0.1")
        val server = Endpoint.createServerEndpoint(bindAddr, cert)
        val client = Endpoint.createClientEndpoint(acceptAllCerts = true)

        try {
            val serverJob = launch {
                val conn = server.incomingSessions().first()
                val stream = conn.acceptUni()
                // Read once then stop to create backpressure
                val buf = ByteArray(10)
                try {
                    stream.read(buf)
                    delay(5000) 
                } catch (e: Exception) {}
            }
            
            delay(200)
            
            client.connect("https://127.0.0.1:$port/webtransport").use { conn ->
                val stream = conn.openUni()
                val job = launch {
                    val hugeBuf = ByteArray(1024 * 1024) 
                    try {
                        // Write continuously until blocked/cancelled
                        while (isActive) {
                            stream.write(hugeBuf)
                        }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        // Ignore
                    }
                }
                
                delay(500) // Let it fill buffers
                job.cancelAndJoin()
                assertTrue(job.isCancelled)
            }
            serverJob.cancel()
        } finally {
            server.close()
            client.close()
        }
    }
}
