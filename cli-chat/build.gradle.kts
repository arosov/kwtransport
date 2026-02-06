plugins {
    kotlin("jvm")
    application
}

group = "io.github.arosov.kwtransport"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// Detect current platform for native dependency
val os = System.getProperty("os.name").lowercase()
val arch = System.getProperty("os.arch").lowercase()
val platform = when {
    os.contains("linux") -> if (arch.contains("aarch64") || arch.contains("arm64")) "linux-arm64" else "linux-x64"
    os.contains("mac") -> if (arch.contains("aarch64") || arch.contains("arm64")) "macos-arm64" else "macos-x64"
    os.contains("win") -> "windows-x64"
    else -> "unknown"
}

dependencies {
    implementation("io.github.arosov:kwtransport:0.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    // Explicitly include native runtime for the current host
    if (platform != "unknown") {
        runtimeOnly("io.github.arosov:kwtransport-jvm-$platform:0.0.1")
    }
}

application {
    mainClass.set("ChatKt")
    applicationDefaultJvmArgs = listOf("-Djava.library.path=$projectDir/../kwtransport/build/rust-lib/")
}
