# Multi-Platform FFI Artifact Strategy

To keep library sizes small and follow industry standards, `kwtransport` uses platform-specific artifacts for its native FFI dependencies.

## 1. Goal
Users should only download the native binary (`.so`, `.dylib`, `.dll`) that matches their execution environment.

## 2. Artifact Hierarchy
- **`kwtransport-common`**: Pure Kotlin interfaces and logic (KMP Common).
- **`kwtransport-jvm`**: JVM-specific Kotlin code, depends on a "native provider" artifact.
- **`kwtransport-jvm-linux-x64`**: Resource-only JAR containing the Linux binary.
- **`kwtransport-jvm-macos-arm64`**: Resource-only JAR containing the macOS Apple Silicon binary.
- ... etc for all supported architectures.

## 3. Implementation in Gradle

### A. Defining Publications
In `kwtransport/build.gradle.kts`, we define extra Maven publications for each native platform.

```kotlin
// Example for Linux x64
val linuxX64Jar = tasks.register<Jar>("linuxX64Jar") {
    archiveClassifier.set("linux-x64")
    from(layout.buildDirectory.dir("rust-lib/linux-x64")) {
        into("native/linux-x64")
    }
}

publishing {
    publications {
        create<MavenPublication>("jvmLinuxX64") {
            artifactId = "kwtransport-jvm-linux-x64"
            artifact(linuxX64Jar)
        }
    }
}
```

### B. Variant Selection (Gradle Attributes)
We attach `org.gradle.native.operatingSystem` and `org.gradle.native.machineArchitecture` attributes to these publications. Gradle's dependency engine uses these to automatically select the right JAR at runtime.

## 4. CI Workflow (GitHub Actions)
A matrix build is required to compile Rust on the native host for maximum compatibility.

```yaml
strategy:
  matrix:
    os: [ubuntu-latest, macos-latest, windows-latest]
    include:
      - os: ubuntu-latest
        arch: x64
      - os: macos-latest
        arch: aarch64
```

## 5. Runtime Loading
The Kotlin `NativeLoader` must be robust:
1.  Identify OS: `System.getProperty("os.name")`.
2.  Identify Arch: `System.getProperty("os.arch")`.
3.  Resolve Resource: `/native/${os}-${arch}/libkwtransport_ffi.${ext}`.
4.  Extract: Copy from JAR to a secure temporary directory.
5.  Load: `System.load(tempFile.absolutePath)`.

## 6. Supported Platforms
- **JVM:** Linux (x64, arm64), macOS (x64, arm64), Windows (x64).
- **Android:** arm64-v8a, armeabi-v7a, x86_64 (all bundled in one AAR).
- **JS/WASM:** No native binary needed (uses browser APIs).
