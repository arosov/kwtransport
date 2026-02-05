package io.github.arosov.kwtransport

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StatsTest {

    private fun getPort(endpoint: Endpoint): Int {
        val addr = endpoint.localAddr
        return addr.substringAfterLast(':').toInt()
    }

    @Test
    fun testConnectionStats() = runBlocking {
        println("StatsTest: Starting")
        val cert = Certificate.createSelfSigned("localhost", "127.0.0.1")
        val hash = cert.getHash()

        val server = Endpoint.createServerEndpoint(
            bindAddr = "127.0.0.1:0",
            certificate = cert
        )
        val port = getPort(server)
        val url = "https://127.0.0.1:$port/test"
        println("StatsTest: Server listening on $url")

        val serverJob = launch {
            try {
                server.incomingSessions().collect { connection ->
                    println("StatsTest: Server accepted connection")
                    val stats = connection.getStats()
                    println("StatsTest: Server Stats: $stats")
                    assertNotNull(stats)
                    connection.acceptUni()
                }
            } catch (e: Exception) {
                // Ignore
            }
        }

        try {
            val client = Endpoint.createClientEndpoint(
                certificateHashes = listOf(hash)
            )

            println("StatsTest: Client connecting to $url")
            val connection = client.connect(url)
            println("StatsTest: Client connected")
            val stats = connection.getStats()
            println("StatsTest: Client Stats: $stats")
            assertNotNull(stats)
            
            // Basic sanity check
            assertTrue(stats.rtt >= 0)
            assertTrue(stats.bytesSent >= 0)
            
            connection.close()
            client.close()
        } catch (e: Exception) {
            println("StatsTest ERROR: ${e.message}")
            throw e
        } finally {
            serverJob.cancel()
            server.close()
        }
    }
}
