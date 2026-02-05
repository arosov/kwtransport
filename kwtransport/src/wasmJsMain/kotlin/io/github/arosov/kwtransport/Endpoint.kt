package io.github.arosov.kwtransport

import io.github.arosov.kwtransport.exceptions.KwTransportException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.await
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
actual class Endpoint internal constructor(
    private val certificateHashes: List<String> = emptyList()
) : Closeable {
    actual suspend fun connect(url: String): Connection {
        println("WASM: Connecting to $url")
        try {
            val options = if (certificateHashes.isNotEmpty()) {
                println("WASM: Building options for hashes: ${certificateHashes.joinToString()}")
                val opts = createJsWebTransportOptions()
                val jsHashes = JsArray<JsWebTransportHash>()
                for (i in certificateHashes.indices) {
                    val h = createJsWebTransportHash()
                    h.algorithm = "sha-256"
                    val bytes = hexToBytes(certificateHashes[i])
                    val jsBytes = bytes.toJsUint8Array()
                    println("WASM: Hash $i length: ${uint8Array_length(jsBytes)}")
                    h.value = jsBytes
                    jsHashes.set(i, h)
                }
                opts.serverCertificateHashes = jsHashes
                opts
            } else {
                null
            }

            println("WASM: Instantiating JsWebTransport")
            val jsTransport = try {
                JsWebTransport(url, options)
            } catch (e: Throwable) {
                val errStr = e.message ?: "Constructor failed"
                println("WASM: Constructor threw: $errStr")
                throw KwTransportException("Constructor failed: $errStr")
            }
            
            println("WASM: Waiting for ready promise...")
            try {
                jsTransport.ready.await<JsAny?>()
            } catch (e: Throwable) {
                val errStr = e.message ?: "Ready rejected"
                println("WASM: Ready promise rejected: $errStr")
                throw KwTransportException("Ready rejected: $errStr")
            }
            
            println("WASM: WebTransport ready")
            return Connection(jsTransport)
        } catch (e: Throwable) {
            val finalMsg = if (e is KwTransportException) e.message!! else "Unexpected error: ${e.message}"
            println("WASM: Connection final failure: $finalMsg")
            throw KwTransportException(finalMsg)
        }
    }

    private fun hexToBytes(hex: String): ByteArray {
        val cleanHex = hex.replace(":", "").replace(" ", "")
        val result = ByteArray(cleanHex.length / 2)
        for (i in 0 until cleanHex.length step 2) {
            val byte = cleanHex.substring(i, i + 2).toInt(16).toByte()
            result[i / 2] = byte
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
