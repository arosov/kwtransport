package io.github.arosov.kwtransport

import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import io.github.arosov.kwtransport.exceptions.KwTransportException
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
actual class RecvStream internal constructor(private val reader: JsReadableStreamDefaultReader) : Closeable {
    actual suspend fun read(buffer: ByteArray): Int {
        try {
            val result: JsReadResult = reader.read().await()
            if (result.done) {
                return -1
            }
            result.value?.let { value ->
                val kotlinArray = value.toByteArray()
                val size = if (kotlinArray.size < buffer.size) kotlinArray.size else buffer.size
                kotlinArray.copyInto(buffer, 0, 0, size)
                return size
            }
            return 0
        } catch (e: Throwable) {
            throw KwTransportException("Failed to read from stream: ${e.message}")
        }
    }

    actual fun chunks(chunkSize: Int): Flow<ByteArray> = flow {
        while (true) {
            try {
                val result: JsReadResult = reader.read().await()
                if (result.done) {
                    break
                }
                result.value?.let { value ->
                    emit(value.toByteArray())
                }
            } catch (e: Throwable) {
                throw KwTransportException("Failed to read chunk from stream: ${e.message}")
            }
        }
    }

    actual override fun close() {
        reader.cancel()
    }
}