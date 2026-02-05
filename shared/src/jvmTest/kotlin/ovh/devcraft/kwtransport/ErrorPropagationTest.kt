package ovh.devcraft.kwtransport

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.test.runTest
import kotlin.test.*
import ovh.devcraft.kwtransport.exceptions.*
import java.net.ServerSocket
import kotlin.time.Duration.Companion.seconds

class ErrorPropagationTest {

    private fun getFreePort(): Int {
        return ServerSocket(0).use { it.localPort }
    }

    @Test
    fun `invalid url should throw ConnectingException`() = runTest {
        val client = Endpoint.createClientEndpoint()
        try {
            val e = assertFailsWith<ConnectingException> {
                client.connect("invalid-url")
            }
            assertEquals(ConnectingErrorType.INVALID_URL, e.type)
        } finally {
            client.close()
        }
    }

    @Test
    fun `connection refused should throw KwTransportException`() = runBlocking {
        // Set short idle timeout (5s) to fail fast on connection issues
        val client = Endpoint.createClientEndpoint(maxIdleTimeoutMillis = 5000)
        try {
            val port = getFreePort()
            withTimeout(30.seconds) {
                val e = assertFailsWith<KwTransportException> {
                    client.connect("https://127.0.0.1:$port/webtransport")
                }
                assertTrue(e is ConnectingException || e is ConnectionException, "Expected Connecting or Connection exception, got $e")
            }
        } finally {
            client.close()
        }
    }

    @Test
    fun `tls verification failure should throw KwTransportException`() = runBlocking {
        val port = getFreePort()
        val bindAddr = "127.0.0.1:$port"
        val cert = Certificate.createSelfSigned("localhost", "127.0.0.1")
        val server = Endpoint.createServerEndpoint(bindAddr, cert)
        
        // Client defaults to acceptAllCerts = false. Set short timeout.
        val client = Endpoint.createClientEndpoint(acceptAllCerts = false, maxIdleTimeoutMillis = 5000)
        
        try {
            withTimeout(30.seconds) {
                val e = assertFailsWith<KwTransportException> {
                    client.connect("https://127.0.0.1:$port/webtransport")
                }
                assertTrue(e is ConnectingException || e is ConnectionException, "Expected Connecting or Connection exception, got $e")
            }
        } finally {
            server.close()
            client.close()
        }
    }
}