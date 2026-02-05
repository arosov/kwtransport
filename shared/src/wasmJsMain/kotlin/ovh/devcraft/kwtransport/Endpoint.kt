package ovh.devcraft.kwtransport

import ovh.devcraft.kwtransport.exceptions.KwTransportException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.await
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
actual class Endpoint internal constructor(
    private val certificateHashes: List<String> = emptyList()
) : Closeable {
    actual suspend fun connect(url: String): Connection {
        try {
            val options = if (certificateHashes.isNotEmpty()) {
                val opts = createJsWebTransportOptions()
                val jsHashes = JsArray<JsWebTransportHash>()
                for (i in certificateHashes.indices) {
                    val h = createJsWebTransportHash()
                    h.algorithm = "sha-256"
                    h.value = hexToJsUint8Array(certificateHashes[i])
                    jsHashes.set(i, h)
                }
                opts.serverCertificateHashes = jsHashes
                opts
            } else {
                null
            }

            val jsTransport = JsWebTransport(url, options)
            jsTransport.ready.await<JsAny?>()
            return Connection(jsTransport)
        } catch (e: Throwable) {
            throw KwTransportException("Failed to connect: ${e.message}")
        }
    }

    private fun hexToJsUint8Array(hex: String): JsUint8Array {
        val cleanHex = hex.replace(":", "").replace(" ", "")
        val bytes = cleanHexToBytes(cleanHex)
        return bytes.toJsUint8Array()
    }

    private fun cleanHexToBytes(hex: String): ByteArray {
        val result = ByteArray(hex.length / 2)
        for (i in 0 until hex.length step 2) {
            result[i / 2] = hex.substring(i, i + 2).toInt(16).toByte()
        }
        return result
    }
    
    actual fun incomingSessions(): Flow<Connection> = flow { 
    }

    actual override fun close() {
    }
}

actual fun createClientEndpoint(
    certificateHashes: List<String>
): Endpoint {
    return Endpoint(certificateHashes)
}

actual fun createServerEndpoint(
    bindAddr: String,
    certificate: Certificate
): Endpoint {
    throw UnsupportedOperationException("WASM target does not support server endpoints.")
}
