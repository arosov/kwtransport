package io.github.arosov.kwtransport

import kotlinx.coroutines.flow.Flow

/**

 * A stream that can be used to receive data from the peer.

 */

expect class RecvStream : Closeable {

    /**

     * Reads data from the stream into the provided [buffer].

     * @return The number of bytes read, or -1 if the stream has ended.

     */

    suspend fun read(buffer: ByteArray): Int



    /**

     * Returns a [Flow] that emits data from the stream in chunks of [chunkSize].

     */

    fun chunks(chunkSize: Int = 8192): Flow<ByteArray>



    /**

     * Closes the receiving side of the stream.

     */

    override fun close()

}
