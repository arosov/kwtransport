package ovh.devcraft.kwtransport

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class PriorityTest {
    @Test
    fun testStreamPriority() = runBlocking {
        val cert = Certificate.createSelfSigned("localhost")
        val serverHash = cert.getHash()
        
        Endpoint.createServerEndpoint("127.0.0.1:0", cert).use { server ->
            val serverAddr = server.localAddr
            
            val serverJob = launch {
                server.incomingSessions().collect { }
            }
            
            Endpoint.createClientEndpoint(
                "127.0.0.1:0",
                certificateHashes = listOf(serverHash)
            ).use { client ->
                val connection = client.connect("https://$serverAddr")
                connection.use {
                    val stream = it.openUni()
                    stream.use { s ->
                        assertEquals(0, s.getPriority(), "Default priority should be 0")
                        
                        s.setPriority(10)
                        assertEquals(10, s.getPriority(), "Priority should be updated to 10")
                        
                        s.setPriority(-5)
                        assertEquals(-5, s.getPriority(), "Negative priority should be supported")
                    }
                }
            }
            serverJob.cancel()
        }
    }
}
