package ovh.devcraft.kwtransport

import kotlinx.coroutines.await
import ovh.devcraft.kwtransport.exceptions.KwTransportException
import org.khronos.webgl.Uint8Array

actual class SendStream internal constructor(private val writer: JsWritableStreamDefaultWriter) : Closeable {
    actual suspend fun write(data: ByteArray) {
        try {
            writer.write(data.toUint8Array()).await()
        } catch (e: Throwable) {
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

private fun ByteArray.toUint8Array(): Uint8Array {
    return Uint8Array(this.asDynamic().buffer as org.khronos.webgl.ArrayBuffer, this.asDynamic().byteOffset as Int, this.size)
}