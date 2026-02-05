package ovh.devcraft.kwtransport

import kotlinx.coroutines.await
import kotlin.js.Promise
import kotlin.js.ExperimentalWasmJsInterop

@JsFun("(size) => new Uint8Array(size)")
internal external fun createUint8Array(size: Int): JsAny

@JsFun("(arr, i, v) => arr[i] = v")
internal external fun uint8Array_set(arr: JsAny, i: Int, v: Int)

@JsFun("(arr, i) => arr[i]")
internal external fun uint8Array_get(arr: JsAny, i: Int): Int

@JsFun("(arr) => arr.length")
internal external fun uint8Array_length(arr: JsAny): Int

internal fun ByteArray.toJsUint8Array(): JsAny {
    val uint8Array = createUint8Array(this.size)
    for (i in indices) {
        uint8Array_set(uint8Array, i, this[i].toInt() and 0xFF)
    }
    return uint8Array
}

internal fun JsAny.toByteArray(): ByteArray {
    val size = uint8Array_length(this)
    val byteArray = ByteArray(size)
    for (i in 0 until size) {
        byteArray[i] = uint8Array_get(this, i).toByte()
    }
    return byteArray
}

// Interop definitions mirroring the browser's WebTransport API

@OptIn(ExperimentalWasmJsInterop::class)
@JsName("WebTransport")
internal external class JsWebTransport(url: String, options: JsWebTransportOptions? = definedExternally) : JsAny {
    val ready: Promise<JsAny?>
    val closed: Promise<JsAny?>
    fun createBidirectionalStream(): Promise<JsWebTransportBidirectionalStream>
    val incomingBidirectionalStreams: JsReadableStream
    fun createUnidirectionalStream(): Promise<JsWritableStream>
    val incomingUnidirectionalStreams: JsReadableStream
    val datagrams: JsWebTransportDatagrams
    fun close()
}

internal external interface JsWebTransportDatagrams : JsAny {
    val readable: JsReadableStream
    val writable: JsWritableStream
    val maxDatagramSize: Int
}

internal external interface JsWebTransportOptions : JsAny {
    var serverCertificateHashes: JsArray<JsWebTransportHash>?
}

internal external interface JsWebTransportHash : JsAny {
    var algorithm: String
    var value: JsAny
}

@JsFun("() => ({})")
internal external fun createJsWebTransportOptions(): JsWebTransportOptions

@JsFun("() => ({})")
internal external fun createJsWebTransportHash(): JsWebTransportHash

internal external interface JsWebTransportBidirectionalStream : JsAny {
    val readable: JsReadableStream
    val writable: JsWritableStream
}

internal external interface JsReadableStream : JsAny {
    fun getReader(): JsReadableStreamDefaultReader
}

internal external interface JsReadableStreamDefaultReader : JsAny {
    fun read(): Promise<JsReadResult>
    fun cancel()
}

internal external interface JsReadResult : JsAny {
    val value: JsAny?
    val done: Boolean
}

internal external interface JsWritableStream : JsAny {
    fun getWriter(): JsWritableStreamDefaultWriter
}

internal external interface JsWritableStreamDefaultWriter : JsAny {
    fun write(chunk: JsAny): Promise<JsAny?>
    fun close(): Promise<JsAny?>
}
