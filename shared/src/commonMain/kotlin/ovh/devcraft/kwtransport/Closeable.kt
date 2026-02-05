package ovh.devcraft.kwtransport

expect interface Closeable : AutoCloseable {
    override fun close()
}
