package ovh.devcraft.kwtransport

import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import ovh.devcraft.kwtransport.exceptions.ConnectionException
import ovh.devcraft.kwtransport.exceptions.ConnectionErrorType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class ErrorTest {

    private fun getPort(endpoint: Endpoint): Int {
        val addr = endpoint.localAddr
        return addr.substringAfterLast(':').toInt()
    }

    @Test
    fun testApplicationCloseError() = runTest {
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
                    println("Server accepted connection")
                    // Wait a bit to ensure handshake is fully processed on both sides
                    kotlinx.coroutines.delay(100)
                    // Close with specific code and reason
                    connection.close(42L, "Goodbye")
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
            println("Client connected")
            
            // Try to accept a stream, should fail with ConnectionException
            val exception = assertFailsWith<ConnectionException> {
                connection.acceptUni()
            }
            
            println("Client caught exception: $exception")
            assertEquals(ConnectionErrorType.APPLICATION_CLOSED, exception.type)
            assertEquals(42L, exception.errorCode)
            assertEquals("Goodbye", exception.reason)
            
            client.close()
        } finally {
            serverJob.cancel()
            server.close()
        }
    }
}
