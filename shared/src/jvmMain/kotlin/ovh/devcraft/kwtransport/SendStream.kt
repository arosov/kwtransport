package ovh.devcraft.kwtransport

import java.util.concurrent.atomic.AtomicLong

class SendStream internal constructor(handle: Long) : AutoCloseable {
    private val handle = AtomicLong(handle)
    companion object {
        init {
            KwTransport
        }

        @JvmStatic
        private external fun write(handle: Long, data: ByteArray, id: Long)

        @JvmStatic
        private external fun destroy(handle: Long)
    }

    suspend fun write(data: ByteArray) {
        val h = handle.get()
        if (h == 0L) throw IllegalStateException("Stream is closed")
        val (id, deferred) = AsyncRegistry.createDeferred()
        write(h, data, id)
        deferred.await()
    }

    override fun close() {
        val h = handle.getAndSet(0L)
        if (h != 0L) {
            destroy(h)
        }
    }
}