package io.github.arosov.kwtransport

import kotlinx.coroutines.launch
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
    fun testConnectionStats() = runTest {
        val cert = Certificate.createSelfSigned("localhost", "127.0.0.1")
        val hash = cert.getHash()

        val server = Endpoint.createServerEndpoint(
            bindAddr = "127.0.0.1:0",
            certificate = cert
        )
        val port = getPort(server)
        val url = "https://127.0.0.1:$port/test"

        val serverJob = launch {
            try {
                server.incomingSessions().collect { connection ->
                    val stats = connection.getStats()
                    println("Server Stats: $stats")
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

            val connection = client.connect(url)
            val stats = connection.getStats()
            println("Client Stats: $stats")
            assertNotNull(stats)
            
            // Basic sanity check
            assertTrue(stats.rtt >= 0)
            assertTrue(stats.bytesSent >= 0)
            
            connection.close()
            client.close()
        } finally {
            serverJob.cancel()
            server.close()
        }
    }
}
