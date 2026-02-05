package ovh.devcraft.kwtransport

import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ovh.devcraft.kwtransport.exceptions.KwTransportException
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.Int8Array

actual class RecvStream internal constructor(private val reader: JsReadableStreamDefaultReader) : Closeable {
    actual suspend fun read(buffer: ByteArray): Int {
        try {
            val result = reader.read().await()
            if (result.done) {
                return -1
            }
            result.value?.let { value ->
                val kotlinArray = value.asByteArray()
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
                result.value?.let { value ->
                    emit(value.asByteArray())
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

private fun Uint8Array.asByteArray(): ByteArray {
    return Int8Array(buffer, byteOffset, length).asDynamic() as ByteArray
}