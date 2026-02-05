package ovh.devcraft.kwtransport

internal actual fun loadNativeLibrary() {
    System.loadLibrary("kwtransport_ffi")
}
