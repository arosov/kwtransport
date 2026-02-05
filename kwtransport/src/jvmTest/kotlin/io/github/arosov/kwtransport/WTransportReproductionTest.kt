package io.github.arosov.kwtransport

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertContentEquals
import java.net.ServerSocket
import kotlin.time.Duration.Companion.seconds

class WTransportReproductionTest {

    private fun getFreePort(): Int {
        return ServerSocket(0).use { it.localPort }
    }

    @Test
    fun `reproduce full features from rust example`() = runTest(timeout = 30.seconds) {
        val port = getFreePort()
        val bindAddr = "127.0.0.1:$port"
        val serverAddr = "https://127.0.0.1:$port/webtransport"

        val cert = Certificate.createSelfSigned("localhost", "127.0.0.1")
        val server = Endpoint.createServerEndpoint(bindAddr, cert)
        val client = Endpoint.createClientEndpoint(acceptAllCerts = true)

        try {
            val serverJob = launch {
                val conn = server.incomingSessions().first()
                
                // Handle Datagram
                val dgram = conn.receiveDatagram()
                if (dgram.contentEquals("PING".toByteArray())) {
                    conn.sendDatagram("PONG".toByteArray())
                }

                // Handle Uni Stream
                // Expect client to open a Uni stream first
                val recvUni = conn.acceptUni()
                val buffer = ByteArray(1024)
                val n = recvUni.read(buffer)
                val msg = buffer.copyOf(n).decodeToString()
                assertEquals("REQUEST", msg)
                
                // Server opens a new Uni stream to reply
                val sendUni = conn.openUni()
                sendUni.write("RESPONSE".toByteArray())
                sendUni.close()
                
                // Handle Bi Stream
                val pair = conn.acceptBi()
                val biBuf = ByteArray(1024)
                val biN = pair.recv.read(biBuf)
                val biMsg = biBuf.copyOf(biN).decodeToString()
                assertEquals("BI-REQUEST", biMsg)
                pair.send.write("BI-RESPONSE".toByteArray())
                pair.send.close()
            }

            // Give server a moment to start
            delay(200)

            client.connect(serverAddr).use { conn ->
                // 1. Test Datagrams
                conn.sendDatagram("PING".toByteArray())
                val responseDgram = conn.receiveDatagram()
                assertEquals("PONG", responseDgram.decodeToString())

                // 2. Test Unidirectional Streams
                val sendUni = conn.openUni()
                sendUni.write("REQUEST".toByteArray())
                sendUni.close() // Close sender to signal we are done writing? 
                                // Or just let server read. 
                                // If we don't close, server might wait for more if it loops.
                                // But server in my code does single read.
                
                // Client must accept the Uni stream opened by server
                val recvUni = conn.acceptUni()
                val buffer = ByteArray(1024)
                val n = recvUni.read(buffer)
                assertEquals("RESPONSE", buffer.copyOf(n).decodeToString())

                // 3. Test Bidirectional Streams
                val pair = conn.openBi()
                pair.send.write("BI-REQUEST".toByteArray())
                pair.send.close()
                
                val biBuf = ByteArray(1024)
                val biN = pair.recv.read(biBuf)
                assertEquals("BI-RESPONSE", biBuf.copyOf(biN).decodeToString())
            }

            serverJob.join()

        } finally {
            withContext(Dispatchers.IO) {
                server.close()
                client.close()
            }
        }
    }
}
