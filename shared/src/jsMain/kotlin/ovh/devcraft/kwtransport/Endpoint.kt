package ovh.devcraft.kwtransport

import ovh.devcraft.kwtransport.exceptions.KwTransportException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.await

actual class Endpoint internal constructor(
    private val certificateHashes: List<String> = emptyList()
) : Closeable {
    actual suspend fun connect(url: String): Connection {
        try {
            val options = if (certificateHashes.isNotEmpty()) {
                val opts = js("{}").unsafeCast<JsWebTransportOptions>()
                opts.serverCertificateHashes = certificateHashes.map { hash ->
                    val h = js("{}").unsafeCast<JsWebTransportHash>()
                    h.algorithm = "sha-256"
                    h.value = hexToUint8Array(hash)
                    h
                }.toTypedArray()
                opts
            } else {
                null
            }

            val jsTransport = if (options != null) JsWebTransport(url, options) else JsWebTransport(url)
            jsTransport.ready.await()
            return Connection(jsTransport)
        } catch (e: Throwable) {
            throw KwTransportException("Failed to connect: ${e.message}")
        }
    }

    private fun hexToUint8Array(hex: String): Uint8Array {
        val cleanHex = hex.replace(":", "").replace(" ", "")
        val bytes = CleanHexToBytes(cleanHex)
        val result = Uint8Array(bytes.size)
        for (i in bytes.indices) {
            result.asDynamic()[i] = bytes[i]
        }
        return result
    }

    private fun CleanHexToBytes(hex: String): ByteArray {
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
    throw UnsupportedOperationException("JS target does not support server endpoints.")
}
