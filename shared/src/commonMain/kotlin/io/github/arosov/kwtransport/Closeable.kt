package io.github.arosov.kwtransport

expect interface Closeable : AutoCloseable {
    override fun close()
}
