package io.github.arosov.kwtransport

import kotlinx.coroutines.await
import io.github.arosov.kwtransport.exceptions.KwTransportException
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
actual class SendStream internal constructor(private val writer: JsWritableStreamDefaultWriter) : Closeable {
    actual suspend fun write(data: ByteArray) {
        println("WASM: SendStream.write started, data size: ${data.size}")
        try {
            val jsData = data.toJsUint8Array()
            println("WASM: Data converted to JsUint8Array")
            val promise = writer.write(jsData)
            println("WASM: writer.write() called, awaiting promise...")
            promise.await<JsAny?>()
            println("WASM: writer.write() promise resolved")
        } catch (e: Throwable) {
            println("WASM: SendStream.write failed: ${e.message}")
            throw KwTransportException("Failed to write to stream: ${e.message}")
        }
    }

    actual suspend fun write(data: String) {
        write(data.encodeToByteArray())
    }

    actual suspend fun setPriority(priority: Int) {
    }

    actual suspend fun getPriority(): Int {
        return 0
    }

    actual override fun close() {
        writer.close()
    }
}