package io.github.arosov.kwtransport

actual interface Closeable : AutoCloseable {

    actual override fun close()

}
