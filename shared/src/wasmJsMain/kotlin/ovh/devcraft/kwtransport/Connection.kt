package ovh.devcraft.kwtransport

import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ovh.devcraft.kwtransport.exceptions.KwTransportException

actual class Connection actual constructor(private val jsTransport: JsWebTransport) : Closeable {
    actual suspend fun openUni(): SendStream {
        throw UnsupportedOperationException("WASM target does not yet support unidirectional streams.")
    }

    actual suspend fun openBi(): StreamPair {
        try {
            val jsStream = jsTransport.createBidirectionalStream().await()
            val recvReader = jsStream.readable.getReader()
            val sendWriter = jsStream.writable.getWriter()
            return StreamPair(SendStream(sendWriter), RecvStream(recvReader))
        } catch (e: dynamic) {
            throw KwTransportException("Failed to open bidirectional stream: ${e.message}", e)
        }
    }

    actual suspend fun acceptUni(): RecvStream {
        throw UnsupportedOperationException("WASM target does not yet support unidirectional streams.")
    }

    actual suspend fun acceptBi(): StreamPair {
        // This is a simplified version. A robust implementation would use a backing Flow
        // from incomingBidirectionalStreams() and pull one item.
        val reader = jsTransport.incomingBidirectionalStreams.getReader()
        try {
            val result = reader.read().await()
            if (result.done) throw KwTransportException("No incoming streams available.")
            val jsStream = result.value.unsafeCast<JsWebTransportBidirectionalStream>()
            val recvReader = jsStream.readable.getReader()
            val sendWriter = jsStream.writable.getWriter()
            return StreamPair(SendStream(sendWriter), RecvStream(recvReader))
        } catch (e: dynamic) {
            throw KwTransportException("Failed to accept bidirectional stream: ${e.message}", e)
        }
    }

    actual fun sendDatagram(data: ByteArray) {
        throw UnsupportedOperationException("WASM target does not yet support datagrams.")
    }

    actual suspend fun receiveDatagram(): ByteArray {
        throw UnsupportedOperationException("WASM target does not yet support datagrams.")
    }

    actual fun getStats(): ConnectionStats {
        console.warn("ConnectionStats are not supported in WASM")
        return ConnectionStats(0, 0, 0, 0, null)
    }

    actual val maxDatagramSize: Long?
        get() = null


    @JsName("closeWithDetails")
    actual fun close(code: Long, reason: String) {
        jsTransport.close()
    }

    actual override fun close() {
        jsTransport.close()
    }

    actual fun isClosed(): Boolean {
        // The JS API uses a promise `closed` to signal this. A synchronous check is not
        // straightforward. Returning false as a placeholder.
        return false
    }
}