package ovh.devcraft.kwtransport

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals

class IntegrationTest {

    @Test
    fun `should perform full bidirectional round trip`() = runTest {
        val serverAddr = "127.0.0.1:4433"
        val cert = withContext(Dispatchers.IO) {
            Certificate.createSelfSigned("localhost", "127.0.0.1")
        }
        
        val serverEndpoint = withContext(Dispatchers.IO) {
            Endpoint.createServerEndpoint(serverAddr, cert)
        }
        
        val clientEndpoint = withContext(Dispatchers.IO) {
            Endpoint.createClientEndpoint(acceptAllCerts = true)
        }

        try {
            // Launch server acceptance in a background coroutine on IO dispatcher
            val serverJob = launch(Dispatchers.IO) {
                serverEndpoint.acceptSession().use { conn ->
                    val pair = conn.acceptBi()
                    val buffer = ByteArray(1024)
                    val n = pair.recv.read(buffer)
                    if (n > 0) {
                        val received = buffer.copyOf(n)
                        pair.send.write(received)
                    }
                }
            }

            // Give the server a moment to start its accept loop
            delay(100)

            withContext(Dispatchers.IO) {
                clientEndpoint.connect("https://$serverAddr/webtransport").use { conn ->
                    val pair = conn.openBi()
                    val data = "Hello World".toByteArray()
                    pair.send.write(data)
                    
                    val buffer = ByteArray(1024)
                    val n = pair.recv.read(buffer)
                    val received = buffer.copyOf(n)
                    
                    assertContentEquals(data, received)
                }
            }

            serverJob.join()
        } finally {
            withContext(Dispatchers.IO) {
                serverEndpoint.close()
                clientEndpoint.close()
            }
        }
    }
}