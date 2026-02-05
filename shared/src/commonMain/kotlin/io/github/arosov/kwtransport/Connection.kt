package io.github.arosov.kwtransport

/**

 * Represents an established WebTransport connection.

 * Allows opening and accepting streams, as well as sending and receiving datagrams.

 */

expect class Connection : Closeable {

    /**

     * Opens a new outgoing unidirectional stream.

     */

    suspend fun openUni(): SendStream



    /**

     * Opens a new outgoing bidirectional stream.

     */

    suspend fun openBi(): StreamPair



    /**

     * Waits for and accepts an incoming unidirectional stream.

     */

    suspend fun acceptUni(): RecvStream



    /**

     * Waits for and accepts an incoming bidirectional stream.

     */

    suspend fun acceptBi(): StreamPair



    /**

     * Sends an unreliable datagram over the connection.

     */

    fun sendDatagram(data: ByteArray)



    /**

     * Waits for and receives an unreliable datagram.

     */

    suspend fun receiveDatagram(): ByteArray



    /**

     * Returns the current connection statistics (RTT, bytes sent/received, etc.).

     */

    fun getStats(): ConnectionStats



    /**

     * The maximum size of a datagram that can be sent over this connection.

     * May be null if not yet determined.

     */

    val maxDatagramSize: Long?



    /**

     * Closes the connection with an optional error [code] and [reason].

     */

    fun close(code: Long = 0, reason: String = "")



    /**

     * Returns true if the connection is closed.

     */

    fun isClosed(): Boolean



    /**

     * Closes the connection and releases all associated resources.

     */

    override fun close()

}
