package ovh.devcraft.kwtransport

expect class SendStream : Closeable {
    suspend fun write(data: ByteArray)
    suspend fun write(data: String)
    suspend fun setPriority(priority: Int)
    suspend fun getPriority(): Int
    override fun close()
}