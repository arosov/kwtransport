package ovh.devcraft.kwtransport

import kotlinx.coroutines.await
import kotlin.js.Promise
import kotlin.js.ExperimentalWasmJsInterop

// Interop definitions mirroring the browser's WebTransport API

@OptIn(ExperimentalWasmJsInterop::class)
@JsName("WebTransport")
internal external class JsWebTransport(url: String) : JsAny {
    val ready: Promise<JsAny?>
    val closed: Promise<JsAny?>
    fun createBidirectionalStream(): Promise<JsWebTransportBidirectionalStream>
    val incomingBidirectionalStreams: JsReadableStream
    fun close()
}

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
    val value: JsUint8Array?
    val done: Boolean
}

internal external interface JsWritableStream : JsAny {
    fun getWriter(): JsWritableStreamDefaultWriter
}

internal external interface JsWritableStreamDefaultWriter : JsAny {
    fun write(chunk: JsUint8Array): Promise<JsAny?>
    fun close(): Promise<JsAny?>
}

@JsName("Uint8Array")
internal external class JsUint8Array : JsAny {
    constructor(length: Int)
    constructor(buffer: JsAny)
    val length: Int
}

internal fun ByteArray.toJsUint8Array(): JsUint8Array {
    val uint8Array = JsUint8Array(this.size)
    for (i in indices) {
        uint8Array_set(uint8Array, i, this[i])
    }
    return uint8Array
}

@JsFun("(arr, i, v) => arr[i] = v")
private external fun uint8Array_set(arr: JsUint8Array, i: Int, v: Byte)

internal fun JsUint8Array.toByteArray(): ByteArray {
    val size = this.length
    val byteArray = ByteArray(size)
    for (i in 0 until size) {
        byteArray[i] = uint8Array_get(this, i)
    }
    return byteArray
}

@JsFun("(arr, i) => arr[i]")
private external fun uint8Array_get(arr: JsUint8Array, i: Int): Byte
