package ovh.devcraft.kwtransport

expect class SendStream : Closeable {
    suspend fun write(data: ByteArray)
    suspend fun write(data: String, charset: Charset)
    suspend fun setPriority(priority: Int)
    suspend fun getPriority(): Int
}