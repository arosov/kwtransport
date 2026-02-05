package io.github.arosov.kwtransport

import io.github.arosov.kwtransport.exceptions.ConnectingException
import io.github.arosov.kwtransport.exceptions.ConnectingErrorType
import io.github.arosov.kwtransport.exceptions.KwTransportException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ConnectionTest {
    @Test
    fun testConnectInvalidUrl() = runTest {
        val endpoint = createClientEndpoint()
        // On browser, this might fail immediately in the constructor or during connect.
        // We wrap it in KwTransportException for consistency.
        try {
            val exception = assertFailsWith<KwTransportException> {
                endpoint.connect("invalid-url")
            }
            // If it's specifically a ConnectingException, verify the type
            if (exception is ConnectingException) {
                assertEquals(ConnectingErrorType.INVALID_URL, exception.type)
            }
        } finally {
            endpoint.close()
        }
    }

    @Test
    fun testConnectConnectionRefused() = runTest {
        val endpoint = createClientEndpoint()
        
        assertFailsWith<KwTransportException> {
            endpoint.connect("https://127.0.0.1:12345/webtransport")
        }
        endpoint.close()
    }
}
