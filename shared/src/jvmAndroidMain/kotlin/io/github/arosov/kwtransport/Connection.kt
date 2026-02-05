package io.github.arosov.kwtransport

import java.util.concurrent.atomic.AtomicLong

actual class Connection internal constructor(handle: Long) : Closeable {
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

    actual suspend fun openUni(): SendStream {
        val h = handle.get()
        if (h == 0L) throw IllegalStateException("Connection is closed")
        val (id, deferred) = AsyncRegistry.createDeferred()
        openUni(h, id)
        val streamHandle = deferred.await()
        return SendStream(streamHandle)
    }

    actual suspend fun openBi(): StreamPair {
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

    actual suspend fun acceptUni(): RecvStream {
        val h = handle.get()
        if (h == 0L) throw IllegalStateException("Connection is closed")
        val (id, deferred) = AsyncRegistry.createDeferred()
        acceptUni(h, id)
        val streamHandle = deferred.await()
        return RecvStream(streamHandle)
    }

    actual suspend fun acceptBi(): StreamPair {
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

    actual fun sendDatagram(data: ByteArray) {
        val h = handle.get()
        if (h == 0L) throw IllegalStateException("Connection is closed")
        sendDatagram(h, data)
    }

    actual suspend fun receiveDatagram(): ByteArray {
        val h = handle.get()
        if (h == 0L) throw IllegalStateException("Connection is closed")
        val (id, deferred) = AsyncRegistry.createDeferred()
        receiveDatagram(h, id)
        val datagramHandle = deferred.await()
        val data = DatagramHelper.getData(datagramHandle)
        DatagramHelper.destroy(datagramHandle)
        return data
    }

    actual fun getStats(): ConnectionStats {
        val h = handle.get()
        if (h == 0L) throw IllegalStateException("Connection is closed")
        return getStats(h)
    }

    actual val maxDatagramSize: Long?
        get() {
            val h = handle.get()
            if (h == 0L) throw IllegalStateException("Connection is closed")
            val size = maxDatagramSize(h)
            return if (size < 0) null else size
        }

    actual fun close(code: Long, reason: String) {
        val h = handle.getAndSet(0L)
        if (h != 0L) {
            close(h, code, reason)
            destroy(h)
        }
    }

    actual override fun close() {
        close(0L, "")
    }

    actual fun isClosed(): Boolean = handle.get() == 0L
}