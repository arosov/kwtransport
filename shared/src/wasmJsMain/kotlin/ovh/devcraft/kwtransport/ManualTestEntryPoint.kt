package ovh.devcraft.kwtransport

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.js.JsExport

@JsExport
fun runManualIntegrationTest(url: String, fingerprint: String) {
    println("MANUAL_TEST: Starting test...")
    println("MANUAL_TEST: URL: $url")
    println("MANUAL_TEST: Fingerprint: $fingerprint")

    GlobalScope.launch {
        try {
            val endpoint = createClientEndpoint(certificateHashes = listOf(fingerprint))
            println("MANUAL_TEST: Endpoint created, connecting...")
            
            val connection = endpoint.connect(url)
            println("MANUAL_TEST: Connected successfully!")
            
            val pair = connection.openBi()
            println("MANUAL_TEST: Bidirectional stream opened")
            
            val data = "Hello from real browser".encodeToByteArray()
            println("MANUAL_TEST: Sending data...")
            pair.send.write(data)
            
            val buffer = ByteArray(data.size)
            println("MANUAL_TEST: Reading response...")
            val n = pair.recv.read(buffer)
            
            println("MANUAL_TEST: Received $n bytes: ${buffer.decodeToString()}")
            
            connection.close()
            endpoint.close()
            println("MANUAL_TEST: Test completed successfully ✅")
        } catch (e: Exception) {
            println("MANUAL_TEST: ❌ Test failed: ${e.message}")
            e.printStackTrace()
        }
    }
}

fun main() {
    println("WASM module initialized. Call 'runManualIntegrationTest(url, fingerprint)' from console.")
}
