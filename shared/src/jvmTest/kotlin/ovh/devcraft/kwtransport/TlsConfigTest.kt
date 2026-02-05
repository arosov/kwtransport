package ovh.devcraft.kwtransport

import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.delay
import kotlin.test.Test
import kotlin.test.assertNotNull

class TlsConfigTest {
    private fun getPort(endpoint: Endpoint): Int {
        val addr = endpoint.localAddr
        return addr.substringAfterLast(':').toInt()
    }

    @Test
    fun testCustomRootCerts() = runTest {
        val cert = Certificate.createSelfSigned("localhost", "127.0.0.1")
        val server = Endpoint.createServerEndpoint(
            bindAddr = "127.0.0.1:0",
            certificate = cert.copy()
        )
        val port = getPort(server)
        val url = "https://127.0.0.1:$port/test"

        val serverJob = launch {
            try {
                server.incomingSessions().collect { connection ->
                    connection.acceptUni()
                }
            } catch (e: Exception) {
            }
        }

        try {
            // Instead of certificateHashes (pinning), we use rootCerts (trusting the CA)
            val client = Endpoint.createClientEndpoint(
                rootCerts = listOf(cert.copy())
            )

            println("Client connecting to $url with custom root CA")
            val connection = client.connect(url)
            assertNotNull(connection)
            println("Client connected successfully using custom root CA")
            
            connection.close()
            client.close()
        } catch (e: Exception) {
            println("Test failed with exception: $e")
            throw e
        } finally {
            serverJob.cancel()
            server.close()
            cert.close()
        }
    }

    @Test
    fun testClientAuth() = runTest {
        // Since we cannot easily configure the server to REQUIRE client auth yet
        // we just test that providing a client cert doesn't break connection
        val cert = Certificate.createSelfSigned("localhost", "127.0.0.1")
        val server = Endpoint.createServerEndpoint(
            bindAddr = "127.0.0.1:0",
            certificate = cert.copy()
        )
        val port = getPort(server)
        val url = "https://127.0.0.1:$port/test"

        val serverJob = launch {
            try {
                server.incomingSessions().collect { connection ->
                    connection.acceptUni()
                }
            } catch (e: Exception) {
            }
        }

        try {
            val client = Endpoint.createClientEndpoint(
                acceptAllCerts = true,
                clientCertificate = cert.copy()
            )

            println("Client connecting to $url with client certificate")
            val connection = client.connect(url)
            assertNotNull(connection)
            println("Client connected successfully with client certificate (even if server didn't ask)")
            
            connection.close()
            client.close()
        } catch (e: Exception) {
            println("Test failed with exception: $e")
            throw e
        } finally {
            serverJob.cancel()
            server.close()
            cert.close()
        }
    }
}
