# Technology Stack

## Core Languages
- **Kotlin:** The primary language for the public API and all target-specific implementations (Android, JVM, WASM).
- **Rust:** The language used for the core WebTransport logic, leveraging the high-performance `wtransport` crate.

## Target Platforms
- **Android:** Targeting mobile devices.
- **JVM (Desktop/Server):** Targeting desktop applications and backend services.
- **WASM (Web):** Targeting modern web browsers via Kotlin/Wasm.

## FFI Layer (Foreign Function Interface)
- **Robusta:** Used to implement the high-performance JNI bridge between Kotlin and Rust.
    - **JNI Backend:** Specifically chosen to ensure maximum performance and avoid the overhead of JNA (which UniFFI uses by default). UniFFI was initially considered but discarded due to this performance requirement.

## Core Libraries & Frameworks
- **wtransport:** The underlying Rust implementation of the WebTransport protocol.
- **Ktor:** Used for the server-side component of the project.
- **Compose Multiplatform:** The UI framework for cross-platform application development.

## Asynchronous Runtime
- **Kotlin Coroutines:** The standard for asynchronous programming in Kotlin.
- **Tokio:** The asynchronous runtime used within the Rust core to handle non-blocking I/O.

## Build System
- **Gradle:** Manages the Kotlin/KMP build process.
- **Cargo:** Manages the Rust build process and dependencies.
