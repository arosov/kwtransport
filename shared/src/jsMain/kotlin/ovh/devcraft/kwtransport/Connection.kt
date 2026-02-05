package ovh.devcraft.kwtransport

import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ovh.devcraft.kwtransport.exceptions.KwTransportException

actual class Connection internal constructor(private val jsTransport: JsWebTransport) : Closeable {
    actual suspend fun openUni(): SendStream {
        throw UnsupportedOperationException("JS target does not yet support unidirectional streams.")
    }

    actual suspend fun openBi(): StreamPair {
        try {
            val jsStream = jsTransport.createBidirectionalStream().await()
            val recvReader = jsStream.readable.getReader()
            val sendWriter = jsStream.writable.getWriter()
            return StreamPair(SendStream(sendWriter), RecvStream(recvReader))
        } catch (e: Throwable) {
            throw KwTransportException("Failed to open bidirectional stream: ${e.message}")
        }
    }

    actual suspend fun acceptUni(): RecvStream {
        throw UnsupportedOperationException("JS target does not yet support unidirectional streams.")
    }

    actual suspend fun acceptBi(): StreamPair {
        val reader = jsTransport.incomingBidirectionalStreams.getReader()
        try {
            val result = reader.read().await()
            if (result.done) throw KwTransportException("No incoming streams available.")
            val jsStream = result.value.asDynamic() as JsWebTransportBidirectionalStream
            val recvReader = jsStream.readable.getReader()
            val sendWriter = jsStream.writable.getWriter()
            return StreamPair(SendStream(sendWriter), RecvStream(recvReader))
        } catch (e: Throwable) {
            throw KwTransportException("Failed to accept bidirectional stream: ${e.message}")
        }
    }

    actual fun sendDatagram(data: ByteArray) {
        throw UnsupportedOperationException("JS target does not yet support datagrams.")
    }

    actual suspend fun receiveDatagram(): ByteArray {
        throw UnsupportedOperationException("JS target does not yet support datagrams.")
    }

    actual fun getStats(): ConnectionStats {
        return ConnectionStats(0, 0, 0, 0, null)
    }

    actual val maxDatagramSize: Long?
        get() = null

    actual fun close(code: Long, reason: String) {
        jsTransport.close()
    }

    actual override fun close() {
        jsTransport.close()
    }

    actual fun isClosed(): Boolean {
        return false
    }
}