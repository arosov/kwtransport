package io.github.arosov.kwtransport

import java.io.File
import java.nio.file.Files

internal object NativeLoader {
    private var loaded = false

    fun load() {
        if (loaded) return
        
        val os = System.getProperty("os.name").lowercase()
        val arch = System.getProperty("os.arch").lowercase()
        
        val (platform, extension) = when {
            os.contains("linux") -> {
                val p = when {
                    arch.contains("aarch64") || arch.contains("arm64") -> "linux-arm64"
                    else -> "linux-x64"
                }
                p to "so"
            }
            os.contains("mac") -> {
                val p = when {
                    arch.contains("aarch64") || arch.contains("arm64") -> "macos-arm64"
                    else -> "macos-x64"
                }
                p to "dylib"
            }
            os.contains("win") -> "windows-x64" to "dll"
            else -> throw RuntimeException("Unsupported operating system: $os")
        }

        val libName = if (extension == "dll") "kwtransport_ffi.dll" else "libkwtransport_ffi.$extension"
        val resourcePath = "/native/$platform/$libName"
        
        val inputStream = javaClass.getResourceAsStream(resourcePath)
            ?: throw RuntimeException("Native library not found in classpath: $resourcePath. Make sure you have the platform-specific dependency for $platform added to your project.")

        val tempDir = Files.createTempDirectory("kwtransport-native").toFile()
        tempDir.deleteOnExit()
        val tempFile = File(tempDir, libName)
        tempFile.deleteOnExit()

        inputStream.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        System.load(tempFile.absolutePath)
        loaded = true
    }
}
