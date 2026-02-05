package io.github.arosov.kwtransport

internal actual fun loadNativeLibrary() {
    System.loadLibrary("kwtransport_ffi")
}
