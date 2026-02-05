package ovh.devcraft.kwtransport

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    fun chunks(chunkSize: Int = 8192): Flow<ByteArray> {
        require(chunkSize > 0) { "chunkSize must be positive" }
        return flow {
            val buffer = ByteArray(chunkSize)
            while (true) {
                val bytesRead = read(buffer)
                if (bytesRead <= 0) {
                    break
                }
                emit(buffer.copyOf(bytesRead))
            }
        }
    }

    override fun close() {
        val h = handle.getAndSet(0L)
        if (h != 0L) {
            destroy(h)
        }
    }
}