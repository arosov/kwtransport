package ovh.devcraft.kwtransport

actual class Certificate {
    actual companion object {
        actual fun createSelfSigned(vararg sans: String): Certificate {
            console.warn("Certificates are not supported in WASM")
            return Certificate()
        }
    }
    actual fun getHash(): String {
        return ""
    }
    actual fun close() {}
}
