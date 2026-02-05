package io.github.arosov.kwtransport

import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import io.github.arosov.kwtransport.exceptions.KwTransportException

actual class Connection internal constructor(private val jsTransport: JsWebTransport) : Closeable {
    private var incomingUniReader: JsReadableStreamDefaultReader? = null
    private var incomingBiReader: JsReadableStreamDefaultReader? = null
    private var datagramReader: JsReadableStreamDefaultReader? = null
    private var datagramWriter: JsWritableStreamDefaultWriter? = null
    private var _isClosed = false

    init {
        jsTransport.closed.then {
            _isClosed = true
        }
    }

    actual suspend fun openUni(): SendStream {
        try {
            val jsStream = jsTransport.createUnidirectionalStream().await()
            val sendWriter = jsStream.getWriter()
            return SendStream(sendWriter)
        } catch (e: Throwable) {
            throw KwTransportException("Failed to open unidirectional stream: ${e.message}")
        }
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
        val reader = incomingUniReader ?: jsTransport.incomingUnidirectionalStreams.getReader().also { incomingUniReader = it }
        try {
            val result = reader.read().await()
            if (result.done) throw KwTransportException("No incoming unidirectional streams available.")
            val jsStream = result.value.asDynamic() as JsReadableStream
            val recvReader = jsStream.getReader()
            return RecvStream(recvReader)
        } catch (e: Throwable) {
            throw KwTransportException("Failed to accept unidirectional stream: ${e.message}")
        }
    }

    actual suspend fun acceptBi(): StreamPair {
        val reader = incomingBiReader ?: jsTransport.incomingBidirectionalStreams.getReader().also { incomingBiReader = it }
        try {
            val result = reader.read().await()
            if (result.done) throw KwTransportException("No incoming bidirectional streams available.")
            val jsStream = result.value.asDynamic() as JsWebTransportBidirectionalStream
            val recvReader = jsStream.readable.getReader()
            val sendWriter = jsStream.writable.getWriter()
            return StreamPair(SendStream(sendWriter), RecvStream(recvReader))
        } catch (e: Throwable) {
            throw KwTransportException("Failed to accept bidirectional stream: ${e.message}")
        }
    }

    actual fun sendDatagram(data: ByteArray) {
        val writer = datagramWriter ?: jsTransport.datagrams.writable.getWriter().also { datagramWriter = it }
        writer.write(data.toUint8Array())
    }

    actual suspend fun receiveDatagram(): ByteArray {
        val reader = datagramReader ?: jsTransport.datagrams.readable.getReader().also { datagramReader = it }
        try {
            val result = reader.read().await()
            if (result.done) throw KwTransportException("Datagram stream closed.")
            return result.value!!.asByteArray()
        } catch (e: Throwable) {
            throw KwTransportException("Failed to receive datagram: ${e.message}")
        }
    }

    actual fun getStats(): ConnectionStats {
        return ConnectionStats(0, 0, 0, 0, null)
    }

    actual val maxDatagramSize: Long?
        get() = jsTransport.datagrams.maxDatagramSize.toLong()

    actual fun close(code: Long, reason: String) {
        jsTransport.close()
    }

    actual override fun close() {
        incomingUniReader?.cancel()
        incomingBiReader?.cancel()
        datagramReader?.cancel()
        jsTransport.close()
    }

    actual fun isClosed(): Boolean {
        return _isClosed
    }
}