# Using UniFFI for Rust JNI Wrappers (Android/Kotlin)

This guide provides a general overview and practical steps for using **UniFFI** to generate Kotlin bindings (JNI wrappers) for a Rust crate, specifically targeting Android. It consolidates information from the official UniFFI documentation and community tutorials.

## 1. General Idea

**UniFFI** (Unified Foreign Function Interface) automates the process of creating bindings between Rust and other languages (Kotlin, Swift, Python). Instead of manually writing JNI (Java Native Interface) code—which is error-prone and verbose—you define your interface once, and UniFFI generates:
1.  **Rust Scaffolding:** Code that exposes your Rust logic to the foreign language.
2.  **Foreign Bindings:** Kotlin (or Swift/Python) code that wraps the native calls in a user-friendly API.

This ensures that types are marshaled correctly, memory is managed safely, and changes in the Rust API are easily propagated to the consumers.

## 2. Core Workflow

1.  **Define Interface:** Describe the API you want to expose using **UDL** (UniFFI Definition Language, similar to WebIDL) or Rust **proc-macros**.
2.  **Implement Rust Logic:** Write the standard Rust code that matches the interface.
3.  **Generate Scaffolding:** UniFFI generates Rust code to handle the FFI boundary.
4.  **Compile Library:** Build your Rust crate as a dynamic library (`cdylib`), resulting in `.so` files for Android.
5.  **Generate Bindings:** Run `uniffi-bindgen` to create the Kotlin files that call into the shared library.
6.  **Integrate:** Add the `.so` files and generated Kotlin code to your Android project.

## 3. Step-by-Step Implementation

### A. Rust Setup

1.  **Create a Rust Library:**
    ```bash
    cargo new my_rust_lib --lib
    ```
2.  **Configure `Cargo.toml`:**
    Set the crate type to `cdylib` and add `uniffi` dependencies.
    ```toml
    [package]
    name = "my_rust_lib"
    version = "0.1.0"
    edition = "2021"

    [lib]
    crate-type = ["cdylib"]
    name = "my_rust_lib" # This becomes libmy_rust_lib.so

    [dependencies]
    uniffi = { version = "0.28" } # Check for latest version

    [build-dependencies]
    uniffi = { version = "0.28", features = ["build"] }
    ```

### B. Define the Interface (UDL Approach)

Create a `src/my_lib.udl` file:
```webidl
namespace my_lib {
    string greet(string name);
    u32 add(u32 a, u32 b);
};
```

*Note: UniFFI also supports a proc-macro approach where you annotate Rust code directly, avoiding a separate UDL file.*

### C. Connect Scaffolding

In `build.rs`:
```rust
fn main() {
    uniffi::generate_scaffolding("./src/my_lib.udl").unwrap();
}
```

In `src/lib.rs`:
```rust
uniffi::include_scaffolding!("my_lib");

fn greet(name: String) -> String {
    format!("Hello, {}!", name)
}

fn add(a: u32, b: u32) -> u32 {
    a + b
}
```

### D. Build for Android

You need to compile the library for Android architectures (`aarch64`, `armv7`, `x86`, `x86_64`).
1.  **Install Targets:**
    ```bash
    rustup target add aarch64-linux-android armv7-linux-androideabi ...
    ```
2.  **Build:**
    ```bash
    cargo build --lib --release --target aarch64-linux-android
    # Repeat for other targets
    ```
    This produces `target/aarch64-linux-android/release/libmy_rust_lib.so`.

### E. Generate Kotlin Bindings

You need the `uniffi-bindgen` binary. You can define it in `Cargo.toml` or run it via cargo.
```bash
cargo run --features=uniffi/cli --bin uniffi-bindgen generate src/my_lib.udl --language kotlin --out-dir out/
```
This generates `my_lib.kt`.

### F. Android Integration

1.  **Copy Shared Libraries:** Place the `.so` files in your Android project's `src/main/jniLibs/<abi>/` folders (e.g., `src/main/jniLibs/arm64-v8a/libmy_rust_lib.so`).
2.  **Copy Kotlin Code:** Copy generated `.kt` files to your source set (e.g., `src/main/java/com/example/`).
3.  **Dependencies:**
    In your app's `build.gradle.kts`:
    ```kotlin
    dependencies {
        implementation("net.java.dev.jna:jna:5.13.0@aar") // Required by UniFFI
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4") // If using async
    }
    ```
4.  **Usage:**
    ```kotlin
    // The library is usually loaded automatically by the generated code, 
    // or you might need System.loadLibrary("my_rust_lib")
    val greeting = uniffi.my_lib.greet("World")
    ```

## 4. Key Concepts & Configuration

### Lifetimes & Memory
*   **Disposable/AutoCloseable:** Generated Kotlin objects that hold Rust resources implement `AutoCloseable`. You generally need to call `.close()` or use Kotlin's `.use { }` block to free memory immediately, otherwise it waits for the GC (which might be too late for native resources).
*   **Nested Objects:** If an object contains other objects, closing the parent typically handles the children, but check specific behavior for Lists/Maps.

### Configuration (`uniffi.toml`)
You can customize the generation by placing a `uniffi.toml` file next to your crate.
```toml
[bindings.kotlin]
package_name = "com.example.mylib"
generate_immutable_records = true
```

### Async Support
UniFFI maps Rust `Future`s to Kotlin `suspend` functions using `kotlinx.coroutines`. This requires the `kotlinx-coroutines-core` dependency.

### Gradle Integration
There isn't a strict official Gradle plugin yet, but you can register Gradle tasks to run `uniffi-bindgen` automatically during the build process to keep your Kotlin bindings in sync with your Rust code.

## 5. References
*   [UniFFI User Guide](https://mozilla.github.io/uniffi-rs/latest/index.html)
*   [Kotlin Configuration](https://mozilla.github.io/uniffi-rs/latest/kotlin/configuration.html)
*   [Kotlin Gradle Integration](https://mozilla.github.io/uniffi-rs/latest/kotlin/gradle.html)
*   [Intro to Rust on Android with UniFFI (Blog)](https://sal.dev/android/intro-rust-android-uniffi/)
