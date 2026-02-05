package ovh.devcraft.kwtransport

/**
 * Represents a TLS certificate used for server identity or client authentication.
 * Use [createSelfSignedCertificate] or [createCertificateFromPem] to instantiate.
 */
expect class Certificate : Closeable {
    /**
     * Returns the SHA-256 hash of the certificate.
     */
    fun getHash(): String

    /**
     * Releases the native resources associated with this certificate.
     */
    override fun close()
}

/**
 * Generates a new self-signed certificate.
 * @param sans Subject Alternative Names (e.g., "localhost", "127.0.0.1").
 */
expect fun createSelfSignedCertificate(vararg sans: String): Certificate

/**
 * Creates a certificate from PEM-encoded strings.
 * @param certificatePem The PEM-encoded certificate chain.
 * @param privateKeyPem The PEM-encoded PKCS#8 private key.
 */
expect fun createCertificateFromPem(certificatePem: String, privateKeyPem: String): Certificate
