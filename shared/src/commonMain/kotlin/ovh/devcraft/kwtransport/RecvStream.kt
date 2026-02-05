package ovh.devcraft.kwtransport

import kotlinx.coroutines.flow.Flow

expect class RecvStream : Closeable {
    suspend fun read(buffer: ByteArray): Int
    fun chunks(chunkSize: Int = 8192): Flow<ByteArray>
}