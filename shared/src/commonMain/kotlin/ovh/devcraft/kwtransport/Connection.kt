package ovh.devcraft.kwtransport

expect class Connection : Closeable {
    suspend fun openUni(): SendStream
    suspend fun openBi(): StreamPair
    suspend fun acceptUni(): RecvStream
    suspend fun acceptBi(): StreamPair
    fun sendDatagram(data: ByteArray)
    suspend fun receiveDatagram(): ByteArray
    fun getStats(): ConnectionStats
    val maxDatagramSize: Long?
    fun close(code: Long = 0, reason: String = "")
    fun isClosed(): Boolean
    override fun close()
}