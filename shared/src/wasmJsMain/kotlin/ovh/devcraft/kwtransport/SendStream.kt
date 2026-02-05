package ovh.devcraft.kwtransport

import kotlinx.coroutines.await
import ovh.devcraft.kwtransport.exceptions.KwTransportException

actual class SendStream actual constructor(private val writer: JsWritableStreamDefaultWriter) : Closeable {
    actual suspend fun write(data: ByteArray) {
        try {
            writer.write(data.toUint8Array()).await()
        } catch (e: dynamic) {
            throw KwTransportException("Failed to write to stream: ${e.message}", e)
        }
    }

    actual suspend fun write(data: String, charset: Charset) {
        write(data.toByteArray())
    }

    actual suspend fun setPriority(priority: Int) {
        console.warn("Stream priority is not supported in browser WebTransport")
    }

    actual suspend fun getPriority(): Int {
        console.warn("Stream priority is not supported in browser WebTransport")
        return 0
    }

    actual override fun close() {
        writer.close()
    }
}