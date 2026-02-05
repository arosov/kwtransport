package ovh.devcraft.kwtransport

import java.util.concurrent.atomic.AtomicLong

class Certificate internal constructor(handle: Long) : AutoCloseable {
    internal val handle = AtomicLong(handle)

    companion object {
        init {
            KwTransport
        }

        @JvmStatic
        private external fun selfSigned(sans: List<String>): Long

        @JvmStatic
        private external fun getHash(handle: Long): String

        @JvmStatic
        private external fun copy(handle: Long): Long

        @JvmStatic
        private external fun destroy(handle: Long)

        fun createSelfSigned(vararg sans: String): Certificate {
            val handle = selfSigned(sans.toList())
            if (handle == 0L) throw RuntimeException("Failed to create self-signed certificate")
            return Certificate(handle)
        }
    }

    fun getHash(): String {
        val h = handle.get()
        if (h == 0L) throw IllegalStateException("Certificate is closed")
        return getHash(h)
    }

    fun copy(): Certificate {
        val h = handle.get()
        if (h == 0L) throw IllegalStateException("Certificate is closed")
        val newHandle = copy(h)
        return Certificate(newHandle)
    }

    override fun close() {
        val h = handle.getAndSet(0L)
        if (h != 0L) {
            destroy(h)
        }
    }
}
