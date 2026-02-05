package io.github.arosov.kwtransport

internal actual fun loadNativeLibrary() {
    NativeLoader.load()
}
