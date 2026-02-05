package ovh.devcraft.kwtransport

/**

 * A stream that can be used to send data to the peer.

 */

expect class SendStream : Closeable {

    /**

     * Writes the provided [data] to the stream.

     */

    suspend fun write(data: ByteArray)



    /**

     * Encodes the provided [data] string as UTF-8 and writes it to the stream.

     */

    suspend fun write(data: String)



    /**

     * Sets the priority of this stream. Higher values mean higher priority.

     */

    suspend fun setPriority(priority: Int)



    /**

     * Returns the current priority of this stream.

     */

    suspend fun getPriority(): Int



    /**

     * Closes the sending side of the stream.

     */

    override fun close()

}
