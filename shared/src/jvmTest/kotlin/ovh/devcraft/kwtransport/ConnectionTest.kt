package ovh.devcraft.kwtransport

import ovh.devcraft.kwtransport.exceptions.ConnectingException
import ovh.devcraft.kwtransport.exceptions.ConnectionException
import ovh.devcraft.kwtransport.exceptions.ConnectingErrorType
import ovh.devcraft.kwtransport.exceptions.KwTransportException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ConnectionTest {
    @Test
    fun testConnectInvalidUrl() = runTest {
        val endpoint = Endpoint.createClientEndpoint()
        val exception = assertFailsWith<ConnectingException> {
            endpoint.connect("invalid-url")
        }
        assertEquals(ConnectingErrorType.INVALID_URL, exception.type)
        endpoint.close()
    }

    @Test
    fun testConnectConnectionRefused() = runTest {
        // Use a short timeout to avoid hanging the test suite
        val endpoint = Endpoint.createClientEndpoint(acceptAllCerts = true, maxIdleTimeoutMillis = 500L)
        
        assertFailsWith<KwTransportException> {
            endpoint.connect("https://127.0.0.1:12345/webtransport")
        }
        endpoint.close()
    }
}
