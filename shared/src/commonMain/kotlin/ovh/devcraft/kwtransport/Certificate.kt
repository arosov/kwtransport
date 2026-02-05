package ovh.devcraft.kwtransport

expect class Certificate : Closeable {
    fun getHash(): String
    override fun close()
}

expect fun createSelfSignedCertificate(vararg sans: String): Certificate
