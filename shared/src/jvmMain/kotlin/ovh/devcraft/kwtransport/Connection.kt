package ovh.devcraft.kwtransport

import java.util.concurrent.atomic.AtomicLong

class Connection internal constructor(handle: Long) : AutoCloseable {
    private val handle = AtomicLong(handle)
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

        @JvmStatic
        private external fun getStats(handle: Long): ConnectionStats

        @JvmStatic
        private external fun maxDatagramSize(handle: Long): Long

        @JvmStatic
        private external fun close(handle: Long, code: Long, reason: String)
    }

    suspend fun openUni(): SendStream {
        val h = handle.get()
        if (h == 0L) throw IllegalStateException("Connection is closed")
        val (id, deferred) = AsyncRegistry.createDeferred()
        openUni(h, id)
        val streamHandle = deferred.await()
        return SendStream(streamHandle)
    }

    suspend fun openBi(): StreamPair {
        val h = handle.get()
        if (h == 0L) throw IllegalStateException("Connection is closed")
        val (id, deferred) = AsyncRegistry.createDeferred()
        openBi(h, id)
        val pairHandle = deferred.await()
        val send = SendStream(StreamPairHelper.getSend(pairHandle))
        val recv = RecvStream(StreamPairHelper.getRecv(pairHandle))
        StreamPairHelper.destroy(pairHandle)
        return StreamPair(send, recv)
    }

    suspend fun acceptUni(): RecvStream {
        val h = handle.get()
        if (h == 0L) throw IllegalStateException("Connection is closed")
        val (id, deferred) = AsyncRegistry.createDeferred()
        acceptUni(h, id)
        val streamHandle = deferred.await()
        return RecvStream(streamHandle)
    }

    suspend fun acceptBi(): StreamPair {
        val h = handle.get()
        if (h == 0L) throw IllegalStateException("Connection is closed")
        val (id, deferred) = AsyncRegistry.createDeferred()
        acceptBi(h, id)
        val pairHandle = deferred.await()
        val send = SendStream(StreamPairHelper.getSend(pairHandle))
        val recv = RecvStream(StreamPairHelper.getRecv(pairHandle))
        StreamPairHelper.destroy(pairHandle)
        return StreamPair(send, recv)
    }

    fun sendDatagram(data: ByteArray) {
        val h = handle.get()
        if (h == 0L) throw IllegalStateException("Connection is closed")
        sendDatagram(h, data)
    }

    suspend fun receiveDatagram(): ByteArray {
        val h = handle.get()
        if (h == 0L) throw IllegalStateException("Connection is closed")
        val (id, deferred) = AsyncRegistry.createDeferred()
        receiveDatagram(h, id)
        val datagramHandle = deferred.await()
        val data = DatagramHelper.getData(datagramHandle)
        DatagramHelper.destroy(datagramHandle)
        return data
    }

    fun getStats(): ConnectionStats {
        val h = handle.get()
        if (h == 0L) throw IllegalStateException("Connection is closed")
        return getStats(h)
    }

    val maxDatagramSize: Long?
        get() {
            val h = handle.get()
            if (h == 0L) throw IllegalStateException("Connection is closed")
            val size = maxDatagramSize(h)
            return if (size < 0) null else size
        }

    @JvmOverloads
    fun close(code: Long = 0L, reason: String = "") {
        val h = handle.getAndSet(0L)
        if (h != 0L) {
            close(h, code, reason)
            destroy(h)
        }
    }

    override fun close() {
        close(0L, "")
    }

    fun isClosed(): Boolean = handle.get() == 0L
}