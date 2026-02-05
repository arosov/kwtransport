package ovh.devcraft.kwtransport

actual interface Closeable : AutoCloseable {

    actual override fun close()

}
