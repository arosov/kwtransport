package ovh.devcraft.kwtransport

class Certificate internal constructor(internal var handle: Long) : AutoCloseable {
    companion object {
        init {
            KwTransport
        }

        @JvmStatic
        private external fun selfSigned(sans: List<String>): Long

        @JvmStatic
        private external fun destroy(handle: Long)

        fun createSelfSigned(vararg sans: String): Certificate {
            val handle = selfSigned(sans.toList())
            if (handle == 0L) throw RuntimeException("Failed to create self-signed certificate")
            return Certificate(handle)
        }
    }

    override fun close() {
        if (handle != 0L) {
            destroy(handle)
            handle = 0L
        }
    }
}
