package ovh.devcraft.kwtransport

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlin.test.Test
import kotlin.test.assertNotNull

class CongestionTest {
    @Test
    fun testSetCongestionController() = runBlocking {
        val cert = Certificate.createSelfSigned("localhost", "127.0.0.1")
        val hash = cert.getHash()
        
        val quicConfig = QuicConfig(
            congestionController = CongestionController.NewReno
        )

        val server = Endpoint.createServerEndpoint(
            bindAddr = "127.0.0.1:0",
            certificate = cert.copy(),
            quicConfig = quicConfig
        )
        val serverAddr = server.localAddr
        val url = "https://$serverAddr/test"

        val serverJob = launch(Dispatchers.IO) {
            try {
                server.incomingSessions().first().use { connection ->
                    delay(1000)
                }
            } catch (e: Exception) {
            }
        }

        try {
            val client = Endpoint.createClientEndpoint(
                certificateHashes = listOf(hash),
                quicConfig = quicConfig
            )

            val connection = client.connect(url)
            assertNotNull(connection, "Connection should be successful with NewReno")
            connection.close()
            client.close()
        } finally {
            serverJob.cancel()
            server.close()
            cert.close()
        }
    }
}
