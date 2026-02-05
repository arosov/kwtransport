package ovh.devcraft.kwtransport

import kotlinx.coroutines.await
import org.khronos.webgl.Uint8Array
import kotlin.js.Promise

// Interop definitions mirroring the browser's WebTransport API

@JsName("WebTransport")
internal external class JsWebTransport(url: String) {
    val ready: Promise<Unit>
    val closed: Promise<Unit>
    fun createBidirectionalStream(): Promise<JsWebTransportBidirectionalStream>
    val incomingBidirectionalStreams: JsReadableStream
    fun close()
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
    val value: Uint8Array?
    val done: Boolean
}

internal external interface JsWritableStream {
    fun getWriter(): JsWritableStreamDefaultWriter
}

internal external interface JsWritableStreamDefaultWriter {
    fun write(chunk: Uint8Array): Promise<Unit>
    fun close(): Promise<Unit>
}

// Extension to convert Kotlin ByteArray to JS Uint8Array
internal fun ByteArray.toUint8Array(): Uint8Array {
    return Uint8Array(this.toTypedArray())
}
