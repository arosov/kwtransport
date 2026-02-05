package ovh.devcraft.kwtransport

class Connection internal constructor(private var handle: Long) : AutoCloseable {
    companion object {
        init {
            KwTransport
        }

        @JvmStatic
        private external fun destroy(handle: Long)

        @JvmStatic
        private external fun openUni(handle: Long): Long

        @JvmStatic
        private external fun openBi(handle: Long): StreamPair

        @JvmStatic
        private external fun acceptUni(handle: Long): Long

        @JvmStatic
        private external fun acceptBi(handle: Long): StreamPair
        
        @JvmStatic
        private external fun sendDatagram(handle: Long, data: ByteArray)
        
        @JvmStatic
        private external fun receiveDatagram(handle: Long): ByteArray
    }

    fun openUni(): SendStream {
        if (handle == 0L) throw IllegalStateException("Connection is closed")
        val streamHandle = openUni(handle)
        return SendStream(streamHandle)
    }

    fun openBi(): StreamPair {
        if (handle == 0L) throw IllegalStateException("Connection is closed")
        return openBi(handle)
    }

    fun acceptUni(): RecvStream {
        if (handle == 0L) throw IllegalStateException("Connection is closed")
        val streamHandle = acceptUni(handle)
        return RecvStream(streamHandle)
    }

    fun acceptBi(): StreamPair {
        if (handle == 0L) throw IllegalStateException("Connection is closed")
        return acceptBi(handle)
    }

    fun sendDatagram(data: ByteArray) {
        if (handle == 0L) throw IllegalStateException("Connection is closed")
        sendDatagram(handle, data)
    }

    fun receiveDatagram(): ByteArray {
        if (handle == 0L) throw IllegalStateException("Connection is closed")
        return receiveDatagram(handle)
    }

    override fun close() {
        if (handle != 0L) {
            destroy(handle)
            handle = 0L
        }
    }

    fun isClosed(): Boolean = handle == 0L
}
