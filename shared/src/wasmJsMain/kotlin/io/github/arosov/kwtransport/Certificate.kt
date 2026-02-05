package io.github.arosov.kwtransport

actual class Certificate : Closeable {
    actual fun getHash(): String {
        return ""
    }
    actual override fun close() {}
}

actual fun createSelfSignedCertificate(vararg sans: String): Certificate {
    return Certificate()
}

actual fun createCertificateFromPem(certificatePem: String, privateKeyPem: String): Certificate {
    return Certificate()
}