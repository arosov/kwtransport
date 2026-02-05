package ovh.devcraft.kwtransport

import java.util.concurrent.atomic.AtomicLong

actual class Certificate internal constructor(handle: Long) : Closeable {
    internal val handle = AtomicLong(handle)

    companion object {
        init {
            KwTransport
        }

        @JvmStatic
        private external fun selfSigned(sans: List<String>): Long

        @JvmStatic
        private external fun fromPem(certPem: String, keyPem: String): Long

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

        fun createFromPem(certPem: String, keyPem: String): Certificate {
            val handle = fromPem(certPem, keyPem)
            if (handle == 0L) throw RuntimeException("Failed to create certificate from PEM")
            return Certificate(handle)
        }
    }

    actual fun getHash(): String {
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

    actual override fun close() {
        val h = handle.getAndSet(0L)
        if (h != 0L) {
            destroy(h)
        }
    }
}

actual fun createSelfSignedCertificate(vararg sans: String): Certificate {
    return Certificate.createSelfSigned(*sans)
}

actual fun createCertificateFromPem(certificatePem: String, privateKeyPem: String): Certificate {
    return Certificate.createFromPem(certificatePem, privateKeyPem)
}