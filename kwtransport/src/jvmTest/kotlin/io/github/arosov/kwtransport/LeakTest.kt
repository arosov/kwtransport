package io.github.arosov.kwtransport

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class LeakTest {
    @Test
    fun `resource accounting should return to zero`() = runBlocking {
        val initialCount = KwTransport.getDiagnosticCount()
        
        val client = Endpoint.createClientEndpoint()
        assertEquals(initialCount + 1, KwTransport.getDiagnosticCount())
        
        client.close()
        assertEquals(initialCount, KwTransport.getDiagnosticCount())
    }
    
    @Test
    fun `server creation with cert should account correctly`() = runBlocking {
        val initialCount = KwTransport.getDiagnosticCount()
        val cert = Certificate.createSelfSigned("localhost")
        assertEquals(initialCount + 1, KwTransport.getDiagnosticCount())
        
        val server = Endpoint.createServerEndpoint("127.0.0.1:0", cert)
        assertEquals(initialCount + 1, KwTransport.getDiagnosticCount())
        
        server.close()
        assertEquals(initialCount, KwTransport.getDiagnosticCount())
    }
}
