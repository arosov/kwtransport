package ovh.devcraft.kwtransport

class SendStream internal constructor(private var handle: Long) : AutoCloseable {
    companion object {
        init {
            KwTransport
        }

        @JvmStatic
        private external fun write(handle: Long, data: ByteArray)

        @JvmStatic
        private external fun destroy(handle: Long)
    }

    fun write(data: ByteArray) {
        if (handle == 0L) throw IllegalStateException("Stream is closed")
        write(handle, data)
    }

    override fun close() {
        if (handle != 0L) {
            destroy(handle)
            handle = 0L
        }
    }
}
