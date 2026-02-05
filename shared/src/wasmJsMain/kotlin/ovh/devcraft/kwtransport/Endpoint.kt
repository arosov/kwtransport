package ovh.devcraft.kwtransport

import ovh.devcraft.kwtransport.exceptions.KwTransportException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

actual class Endpoint : Closeable {
    actual companion object {
        actual fun createClientEndpoint(): Endpoint {
            return Endpoint()
        }

        actual fun createServerEndpoint(
            bindAddr: String,
            certificate: Certificate
        ): Endpoint {
            throw UnsupportedOperationException("WASM target does not support server endpoints.")
        }
    }

    actual suspend fun connect(url: String): Connection {
        try {
            val jsTransport = JsWebTransport(url)
            jsTransport.ready.await() // Wait for the connection to be established
            return Connection(jsTransport)
        } catch (e: dynamic) {
            throw KwTransportException("Failed to connect: ${e.message}", e)
        }
    }
    
    actual fun incomingSessions(): Flow<Connection> = flow { 
        // No-op for client-only WASM
    }

    actual override fun close() {
        // No-op for the WASM endpoint wrapper itself, as the connection
        // is managed by the Connection object.
    }
}