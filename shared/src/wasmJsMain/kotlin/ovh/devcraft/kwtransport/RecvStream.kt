package ovh.devcraft.kwtransport

import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ovh.devcraft.kwtransport.exceptions.KwTransportException

actual class RecvStream actual constructor(private val reader: JsReadableStreamDefaultReader) : Closeable {
    actual suspend fun read(buffer: ByteArray): Int {
        try {
            val result = reader.read().await()
            if (result.done) {
                return -1 // Indicates end of stream
            }
            result.value?.let { value ->
                val jsArray = value
                val kotlinArray = jsArray.asByteArray()
                kotlinArray.copyInto(buffer, 0, 0, kotlinArray.size)
                return kotlinArray.size
            }
            return 0
        } catch (e: dynamic) {
            throw KwTransportException("Failed to read from stream: ${e.message}", e)
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
            } catch (e: dynamic) {
                throw KwTransportException("Failed to read chunk from stream: ${e.message}", e)
            }
        }
    }

    actual override fun close() {
        reader.cancel()
    }
}

// Helper to convert Uint8Array to ByteArray
private fun org.khronos.webgl.Uint8Array.asByteArray(): ByteArray {
    return org.khronos.webgl.Int8Array(buffer, byteOffset, length).unsafeCast<ByteArray>()
}