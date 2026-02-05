package ovh.devcraft.kwtransport

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import java.net.InetAddress
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DnsResolverTest {

    private class MockDnsResolver(private val ipToReturn: String) : CustomDnsResolver {
        val resolveCallCount = AtomicInteger(0)

        override suspend fun resolve(host: String): List<InetAddress> {
            resolveCallCount.incrementAndGet()
            // In a real scenario, you'd perform actual DNS resolution here.
            // For this mock, we always return a fixed IP.
            println("MockDnsResolver: Resolving host \"$host\", returning $ipToReturn")
            return listOf(InetAddress.getByName(ipToReturn))
        }
    }

    @Test
    fun testCustomDnsResolverUsed() = runBlocking {
        val mockIp = "1.1.1.1"
        val mockResolver = MockDnsResolver(mockIp)
        val cert = Certificate.createSelfSigned("localhost", "127.0.0.1")
        val serverHash = cert.getHash()

        // Create a dummy server, client won't actually connect but DNS resolution should happen
        val server = Endpoint.createServerEndpoint(
            bindAddr = "127.0.0.1:0",
            certificate = cert.copy()
        )
        val serverAddr = server.localAddr
        val url = "https://example.com:${serverAddr.substringAfterLast(':')}/test"

        val serverJob = launch(Dispatchers.IO) {
            try {
                server.incomingSessions().collect { connection ->
                    connection.use {
                        delay(1000)
                    }
                }
            } catch (e: Exception) {
                // Ignore
            }
        }

        try {
            val client = Endpoint.createClientEndpoint(
                certificateHashes = listOf(serverHash),
                customDnsResolver = mockResolver // Use the custom resolver
            )

            // Attempt to connect. DNS resolution should be handled by mockResolver.
            // The connection itself might fail since 1.1.1.1 isn't our server, but that's fine.
            try {
                client.connect(url)
            } catch (e: Exception) {
                println("Expected connection failure: ${e.message}")
            }
            
            client.close()

            // Verify that our mock resolver was called
            assertEquals(1, mockResolver.resolveCallCount.get(), "Custom DNS resolver should have been called once")
            
        } finally {
            serverJob.cancel()
            server.close()
            cert.close()
        }
    }
}
