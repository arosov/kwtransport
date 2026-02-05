package io.github.arosov.kwtransport

import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import io.github.arosov.kwtransport.exceptions.KwTransportException
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.Int8Array

actual class RecvStream internal constructor(private val reader: JsReadableStreamDefaultReader) : Closeable {
    actual suspend fun read(buffer: ByteArray): Int {
        try {
            val result = reader.read().await()
            if (result.done) {
                return -1
            }
            val value = result.value
            if (value != null && value != undefined) {
                val kotlinArray = (value as org.khronos.webgl.Uint8Array).asByteArray()
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
                val result = reader.read().await()
                if (result.done) {
                    break
                }
                val value = result.value
                if (value != null && value != undefined) {
                    emit((value as org.khronos.webgl.Uint8Array).asByteArray())
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