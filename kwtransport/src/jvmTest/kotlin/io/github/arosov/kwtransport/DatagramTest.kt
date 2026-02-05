package io.github.arosov.kwtransport

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DatagramTest {
    @Test
    fun testMaxDatagramSize() = runBlocking {
        val cert = Certificate.createSelfSigned("localhost", "127.0.0.1")
        val hash = cert.getHash()
        
        val quicConfig = QuicConfig(
            datagramReceiveBufferSize = 65536L,
            datagramSendBufferSize = 65536L
        )

        val server = Endpoint.createServerEndpoint(
            bindAddr = "127.0.0.1:0",
            certificate = cert,
            quicConfig = quicConfig
        )
        val serverAddr = server.localAddr
        val url = "https://$serverAddr/test"

        val serverJob = launch(Dispatchers.IO) {
            try {
                val connection = server.incomingSessions().first()
                connection.use {
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
            connection.use {
                val maxSize = it.maxDatagramSize
                println("Max Datagram Size: $maxSize")
                assertNotNull(maxSize, "Max datagram size should not be null when supported")
                assertTrue(maxSize > 0, "Max datagram size should be positive")
            }
            client.close()
        } finally {
            serverJob.cancel()
            server.close()
        }
    }
}
