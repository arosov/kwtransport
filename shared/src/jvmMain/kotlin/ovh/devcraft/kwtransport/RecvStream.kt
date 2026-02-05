package ovh.devcraft.kwtransport

class RecvStream internal constructor(private var handle: Long) : AutoCloseable {
    companion object {
        init {
            KwTransport
        }

        @JvmStatic
        private external fun read(handle: Long, buffer: ByteArray): Int

        @JvmStatic
        private external fun destroy(handle: Long)
    }

    fun read(buffer: ByteArray): Int {
        if (handle == 0L) throw IllegalStateException("Stream is closed")
        return read(handle, buffer)
    }

    override fun close() {
        if (handle != 0L) {
            destroy(handle)
            handle = 0L
        }
    }
}
