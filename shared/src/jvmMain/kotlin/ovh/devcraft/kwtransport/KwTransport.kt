package ovh.devcraft.kwtransport

class KwTransport {
    companion object {
        init {
            val osName = System.getProperty("os.name").lowercase()
            val libName = when {
                osName.contains("win") -> "kwtransport_ffi.dll"
                osName.contains("mac") -> "libkwtransport_ffi.dylib"
                else -> "libkwtransport_ffi.so"
            }
            
            try {
                System.loadLibrary("kwtransport_ffi")
            } catch (e: UnsatisfiedLinkError) {
                val libFile = java.io.File("build/rust-lib/$libName")
                if (libFile.exists()) {
                    System.load(libFile.absolutePath)
                } else {
                    // Try searching in the current module's build dir if running from root
                    val localLibFile = java.io.File("shared/build/rust-lib/$libName")
                    if (localLibFile.exists()) {
                        System.load(localLibFile.absolutePath)
                    } else {
                        throw e
                    }
                }
            }
        }

        @JvmStatic
        external fun hello(): String
    }
}