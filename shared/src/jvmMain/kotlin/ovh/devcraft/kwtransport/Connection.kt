package ovh.devcraft.kwtransport

class Connection internal constructor(private var handle: Long) : AutoCloseable {
    companion object {
        init {
            KwTransport
        }

        @JvmStatic
        private external fun destroy(handle: Long)

        @JvmStatic
        private external fun openUni(handle: Long, id: Long)

        @JvmStatic
        private external fun openBi(handle: Long, id: Long)

        @JvmStatic
        private external fun acceptUni(handle: Long, id: Long)

        @JvmStatic
        private external fun acceptBi(handle: Long, id: Long)
        
        @JvmStatic
        private external fun sendDatagram(handle: Long, data: ByteArray)
        
        @JvmStatic
        private external fun receiveDatagram(handle: Long, id: Long)
    }

    suspend fun openUni(): SendStream {
        if (handle == 0L) throw IllegalStateException("Connection is closed")
        val (id, deferred) = AsyncRegistry.createDeferred()
        openUni(handle, id)
        val streamHandle = deferred.await()
        return SendStream(streamHandle)
    }

    suspend fun openBi(): StreamPair {
        if (handle == 0L) throw IllegalStateException("Connection is closed")
        val (id, deferred) = AsyncRegistry.createDeferred()
        openBi(handle, id)
        val pairHandle = deferred.await()
        val send = SendStream(StreamPairHelper.getSend(pairHandle))
        val recv = RecvStream(StreamPairHelper.getRecv(pairHandle))
        StreamPairHelper.destroy(pairHandle)
        return StreamPair(send, recv)
    }

    suspend fun acceptUni(): RecvStream {
        if (handle == 0L) throw IllegalStateException("Connection is closed")
        val (id, deferred) = AsyncRegistry.createDeferred()
        acceptUni(handle, id)
        val streamHandle = deferred.await()
        return RecvStream(streamHandle)
    }

    suspend fun acceptBi(): StreamPair {
        if (handle == 0L) throw IllegalStateException("Connection is closed")
        val (id, deferred) = AsyncRegistry.createDeferred()
        acceptBi(handle, id)
        val pairHandle = deferred.await()
        val send = SendStream(StreamPairHelper.getSend(pairHandle))
        val recv = RecvStream(StreamPairHelper.getRecv(pairHandle))
        StreamPairHelper.destroy(pairHandle)
        return StreamPair(send, recv)
    }

    fun sendDatagram(data: ByteArray) {
        if (handle == 0L) throw IllegalStateException("Connection is closed")
        sendDatagram(handle, data)
    }

    suspend fun receiveDatagram(): ByteArray {
        if (handle == 0L) throw IllegalStateException("Connection is closed")
        val (id, deferred) = AsyncRegistry.createDeferred()
        receiveDatagram(handle, id)
        val datagramHandle = deferred.await()
        val data = DatagramHelper.getData(datagramHandle)
        DatagramHelper.destroy(datagramHandle)
        return data
    }

    override fun close() {
        if (handle != 0L) {
            destroy(handle)
            handle = 0L
        }
    }

    fun isClosed(): Boolean = handle == 0L
}