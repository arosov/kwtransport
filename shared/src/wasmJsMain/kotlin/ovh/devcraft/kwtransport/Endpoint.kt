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
            val jsTransport = JsWebTransport(url)
            jsTransport.ready.await<JsAny?>()
            return Connection(jsTransport)
        } catch (e: Throwable) {
            throw KwTransportException("Failed to connect: ${e.message}")
        }
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
