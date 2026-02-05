package ovh.devcraft.kwtransport

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.fail

class WasmIntegrationTest {
    @Test
    fun testEchoRoundTrip() = runTest {
        println("TEST: Starting WasmIntegrationTest")
        val endpoint = createClientEndpoint(
            certificateHashes = listOf(TestCertificate.fingerprint)
        )
        
        try {
            // Force use 127.0.0.1 to avoid any localhost resolution issues
            val url = "https://127.0.0.1:4433/webtransport"
            println("TEST: Connecting to $url")
            val connection = endpoint.connect(url)
            println("TEST: Connected successfully")
            
            val pair = connection.openBi()
            println("TEST: Bidirectional stream opened")
            
            val data = "Hello from WASM".encodeToByteArray()
            pair.send.write(data)
            println("TEST: Data sent: ${data.size} bytes")
            
            val buffer = ByteArray(data.size)
            var totalRead = 0
            while (totalRead < data.size) {
                val remaining = data.size - totalRead
                val tempBuffer = ByteArray(remaining)
                val n = pair.recv.read(tempBuffer)
                if (n < 0) {
                    println("TEST: Stream ended unexpectedly after $totalRead bytes")
                    break
                }
                if (n > 0) {
                    tempBuffer.copyInto(buffer, totalRead, 0, n)
                    totalRead += n
                    println("TEST: Read chunk of $n bytes, total: $totalRead")
                } else {
                    kotlinx.coroutines.delay(10)
                }
            }
            
            println("TEST: Data received ($totalRead bytes)")
            
            assertEquals(data.size, totalRead, "Read byte count should match sent byte count")
            assertContentEquals(data, buffer, "Echoed data should match sent data")
            
            connection.close()
        } catch (e: Exception) {
            val msg = e.message ?: "Unknown error"
            println("TEST: Integration test failure: $msg")
            fail("WASM Integration Test Failed: $msg")
        } finally {
            endpoint.close()
        }
    }
}