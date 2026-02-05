package ovh.devcraft.kwtransport

import ovh.devcraft.kwtransport.exceptions.ConnectingException
import ovh.devcraft.kwtransport.exceptions.ConnectionException
import ovh.devcraft.kwtransport.exceptions.ConnectingErrorType
import ovh.devcraft.kwtransport.exceptions.KwTransportException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ConnectionTest {
    @Test
    fun testConnectInvalidUrl() {
        val endpoint = Endpoint.createClientEndpoint()
        val exception = assertFailsWith<ConnectingException> {
            endpoint.connect("invalid-url")
        }
        assertEquals(ConnectingErrorType.INVALID_URL, exception.type)
        endpoint.close()
    }

    @Test
    fun testConnectConnectionRefused() {
        val endpoint = Endpoint.createClientEndpoint()
        // Connect to a port that is likely closed
        assertFailsWith<KwTransportException> {
            endpoint.connect("https://127.0.0.1:12345/webtransport")
        }
        endpoint.close()
    }
}