package ovh.devcraft.kwtransport

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.fail

class JsIntegrationTest {
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
            println("TEST: Data sent")
            
            val buffer = ByteArray(data.size)
            val n = pair.recv.read(buffer)
            println("TEST: Data received ($n bytes)")
            
            assertEquals(data.size, n, "Read byte count should match sent byte count")
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