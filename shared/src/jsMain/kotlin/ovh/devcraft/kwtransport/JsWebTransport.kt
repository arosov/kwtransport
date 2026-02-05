package ovh.devcraft.kwtransport

import kotlinx.coroutines.await
import org.khronos.webgl.Uint8Array
import kotlin.js.Promise

@JsName("WebTransport")
internal external class JsWebTransport(url: String, options: JsWebTransportOptions = definedExternally) {
    val ready: Promise<Unit>
    val closed: Promise<Unit>
    fun createBidirectionalStream(): Promise<JsWebTransportBidirectionalStream>
    val incomingBidirectionalStreams: JsReadableStream
    fun createUnidirectionalStream(): Promise<JsWritableStream>
    val incomingUnidirectionalStreams: JsReadableStream
    val datagrams: JsWebTransportDatagrams
    fun close()
}

internal external interface JsWebTransportDatagrams {
    val readable: JsReadableStream
    val writable: JsWritableStream
    val maxDatagramSize: Int
}

internal external interface JsWebTransportOptions {
    var serverCertificateHashes: Array<JsWebTransportHash>?
}

internal external interface JsWebTransportHash {
    var algorithm: String
    var value: Uint8Array
}

internal external interface JsWebTransportBidirectionalStream {
    val readable: JsReadableStream
    val writable: JsWritableStream
}

internal external interface JsReadableStream {
    fun getReader(): JsReadableStreamDefaultReader
}

internal external interface JsReadableStreamDefaultReader {
    fun read(): Promise<JsReadResult>
    fun cancel()
}

internal external interface JsReadResult {
    val value: dynamic
    val done: Boolean
}

internal external interface JsWritableStream {
    fun getWriter(): JsWritableStreamDefaultWriter
}

internal external interface JsWritableStreamDefaultWriter {
    fun write(chunk: Uint8Array): Promise<Unit>
    fun close(): Promise<Unit>
}
