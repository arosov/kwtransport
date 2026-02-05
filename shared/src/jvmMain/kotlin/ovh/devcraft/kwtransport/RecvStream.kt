package ovh.devcraft.kwtransport

class RecvStream internal constructor(private var handle: Long) : AutoCloseable {
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
        if (handle == 0L) throw IllegalStateException("Stream is closed")
        val (id, deferred) = AsyncRegistry.createDeferred()
        read(handle, buffer, id)
        val result = deferred.await()
        return result.toInt()
    }

    override fun close() {
        if (handle != 0L) {
            destroy(handle)
            handle = 0L
        }
    }
}