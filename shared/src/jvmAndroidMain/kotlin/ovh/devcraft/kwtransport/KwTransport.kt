package ovh.devcraft.kwtransport

class KwTransport {
    companion object {
        init {
            loadNativeLibrary()
        }

        @JvmStatic
        external fun hello(): String

        @JvmStatic
        external fun getDiagnosticCount(): Long
    }
}