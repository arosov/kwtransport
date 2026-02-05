package io.github.arosov.kwtransport

import java.util.concurrent.atomic.AtomicLong

actual class SendStream internal constructor(handle: Long) : Closeable {
    private val handle = AtomicLong(handle)
    companion object {
        init {
            KwTransport
        }

        @JvmStatic
        private external fun write(handle: Long, data: ByteArray, id: Long)

        @JvmStatic
        private external fun setPriority(handle: Long, priority: Int, id: Long)

        @JvmStatic
        private external fun getPriority(handle: Long, id: Long)

        @JvmStatic
        private external fun destroy(handle: Long)
    }

    actual suspend fun write(data: ByteArray) {
        val h = handle.get()
        if (h == 0L) throw IllegalStateException("Stream is closed")
        val (id, deferred) = AsyncRegistry.createDeferred()
        write(h, data, id)
        deferred.await()
    }

    actual suspend fun write(data: String) {
        write(data.encodeToByteArray())
    }

    actual suspend fun setPriority(priority: Int) {
        val h = handle.get()
        if (h == 0L) throw IllegalStateException("Stream is closed")
        val (id, deferred) = AsyncRegistry.createDeferred()
        setPriority(h, priority, id)
        deferred.await()
    }

    actual suspend fun getPriority(): Int {
        val h = handle.get()
        if (h == 0L) throw IllegalStateException("Stream is closed")
        val (id, deferred) = AsyncRegistry.createDeferred()
        getPriority(h, id)
        return deferred.await().toInt()
    }

    actual override fun close() {
        val h = handle.getAndSet(0L)
        if (h != 0L) {
            destroy(h)
        }
    }
}