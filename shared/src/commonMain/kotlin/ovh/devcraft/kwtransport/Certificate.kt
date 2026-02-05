package ovh.devcraft.kwtransport

expect class Certificate : Closeable {
    companion object {
        fun createSelfSigned(vararg sans: String): Certificate
    }
    fun getHash(): String
}