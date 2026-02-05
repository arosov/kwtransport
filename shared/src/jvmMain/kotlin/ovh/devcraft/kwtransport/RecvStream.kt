package ovh.devcraft.kwtransport

import java.util.concurrent.atomic.AtomicLong

class RecvStream internal constructor(handle: Long) : AutoCloseable {
    private val handle = AtomicLong(handle)
    companion object {
        init {
            KwTransport
        }

        @JvmStatic
        private external fun read(handle: Long, buffer: ByteArray, id: Long)

        @JvmStatic
        private external fun destroy(handle: Long)
    }

    suspend fun read(buffer: ByteArray): Int {
        val h = handle.get()
        if (h == 0L) throw IllegalStateException("Stream is closed")
        val (id, deferred) = AsyncRegistry.createDeferred()
        read(h, buffer, id)
        val result = deferred.await()
        return result.toInt()
    }

    override fun close() {
        val h = handle.getAndSet(0L)
        if (h != 0L) {
            destroy(h)
        }
    }
}