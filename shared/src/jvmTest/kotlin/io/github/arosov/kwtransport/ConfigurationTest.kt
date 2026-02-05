package io.github.arosov.kwtransport

import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import io.github.arosov.kwtransport.exceptions.KwTransportException
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ConfigurationTest {

    private fun getPort(endpoint: Endpoint): Int {
        val addr = endpoint.localAddr
        return addr.substringAfterLast(':').toInt()
    }

    @Test
    fun testCertificatePinningSuccess() = runTest {
        println("Starting testCertificatePinningSuccess")
        val cert = Certificate.createSelfSigned("localhost", "127.0.0.1")
        val hash = cert.getHash()
        assertNotNull(hash, "Certificate hash should not be null")
        println("Certificate Hash: $hash")

        val server = Endpoint.createServerEndpoint(
            bindAddr = "127.0.0.1:0",
            certificate = cert
        )
        val port = getPort(server)
        println("Server bound to port: $port")
        val url = "https://127.0.0.1:$port/test"

        val serverJob = launch {
            try {
                server.incomingSessions().collect { connection ->
                    println("Server accepted connection")
                    connection.acceptUni()
                }
            } catch (e: Exception) {
                // Ignore cancellation/closing errors
            }
        }

        try {
            val client = Endpoint.createClientEndpoint(
                certificateHashes = listOf(hash)
            )

            println("Client connecting to $url")
            val connection = client.connect(url)
            assertNotNull(connection)
            println("Client connected successfully")
            
            connection.close()
            client.close()
        } catch (e: Exception) {
            println("Test failed with exception: $e")
            throw e
        } finally {
            serverJob.cancel()
            server.close()
        }
    }

    @Test
    fun testCertificatePinningFailure() = runTest {
        println("Starting testCertificatePinningFailure")
        val cert = Certificate.createSelfSigned("localhost", "127.0.0.1")
        val correctHash = cert.getHash()
        val lastChar = correctHash.last()
        val newLastChar = if (lastChar == '0') '1' else '0'
        val fakeHash = correctHash.dropLast(1) + newLastChar
        
        println("Correct Hash: $correctHash")
        println("Fake Hash:    $fakeHash")

        val server = Endpoint.createServerEndpoint(
            bindAddr = "127.0.0.1:0",
            certificate = cert
        )
        val port = getPort(server)
        val url = "https://127.0.0.1:$port/test"
        
        val serverJob = launch {
            try {
                server.incomingSessions().collect { 
                    // Should not happen
                }
            } catch (e: Exception) {
                // Ignore
            }
        }

        try {
            val client = Endpoint.createClientEndpoint(
                certificateHashes = listOf(fakeHash)
            )

            println("Client connecting to $url (expecting failure)")
            assertFailsWith<KwTransportException> {
                client.connect(url)
            }
            println("Client failed to connect as expected")
            
            client.close()
        } finally {
            serverJob.cancel()
            server.close()
        }
    }

    @Test
    fun testQuicConfig() = runTest {
        println("Starting testQuicConfig")
        val cert = Certificate.createSelfSigned("localhost", "127.0.0.1")
        val hash = cert.getHash()

        val quicConfig = QuicConfig(
            maxConcurrentBiStreams = 100L,
            maxConcurrentUniStreams = 100L,
            initialMaxData = 1024 * 1024L,
            datagramReceiveBufferSize = 65536L
        )

        val server = Endpoint.createServerEndpoint(
            bindAddr = "127.0.0.1:0",
            certificate = cert,
            quicConfig = quicConfig
        )
        val port = getPort(server)
        val url = "https://127.0.0.1:$port/test"

        val serverJob = launch {
            try {
                server.incomingSessions().collect { connection ->
                    connection.acceptUni()
                }
            } catch (e: Exception) {
                // Ignore
            }
        }

        try {
            val client = Endpoint.createClientEndpoint(
                certificateHashes = listOf(hash),
                quicConfig = quicConfig
            )

            val connection = client.connect(url)
            assertNotNull(connection)
            
            connection.close()
            client.close()
        } finally {
            serverJob.cancel()
            server.close()
        }
    }

    @Test
    fun testIpv6DualStack() = runTest {
        println("Starting testIpv6DualStack")
        // Use default behavior (0) which is OS default.
        // Explicitly enabling (1) might fail if IPv6 isn't supported on the test runner,
        // so we just test that passing the parameter doesn't crash.
        val cert = Certificate.createSelfSigned("localhost", "127.0.0.1")
        
        val server = Endpoint.createServerEndpoint(
            bindAddr = "[::]:0", // IPv6 Any
            certificate = cert,
            ipv6DualStackConfig = Endpoint.IPV6_DUAL_STACK_DEFAULT
        )
        val port = getPort(server)
        // If dual stack works, we might be able to connect via 127.0.0.1 even if bound to [::]
        // But for safety, we just check server creation succeeded.
        assertTrue(port > 0)
        println("IPv6 Server bound to port: $port")
        
        server.close()
    }
}
