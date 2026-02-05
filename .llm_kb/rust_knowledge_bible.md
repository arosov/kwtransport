# The Rust Knowledge Bible: JVM & Reactive Edition

This document serves as a comprehensive guide for mastering Rust, with a specific focus on building high-performance, reactive JVM wrappers using JNI (via `robusta`).

---

## 1. Rust Fundamentals: The Bedrock

### Ownership and Borrowing
The core of Rust's safety.
- **Ownership**: Each value has a single owner. When the owner goes out of scope, the value is dropped.
- **Borrowing**: 
    - `&T`: Immutable reference (multiple allowed).
    - `&mut T`: Mutable reference (only one allowed, no immutable references simultaneously).
- **Lifetimes**: Ensure references are always valid. In JNI, lifetimes often relate to the `JNIEnv`.

### Structs, Enums, and Traits
- **Structs**: Data containers.
- **Enums**: Sum types (can hold data). `Option<T>` and `Result<T, E>` are the most critical.
- **Traits**: Define shared behavior (interfaces). `Send` and `Sync` are vital for concurrency.

```rust
pub trait ReactiveProcessor {
    fn process(&self, data: String) -> Result<u32, String>;
}
```

---

## 2. Advanced Rust: Power and Precision

### Error Handling: The `Result` Pattern
Never use exceptions. Use `Result<T, E>` and the `?` operator for propagation. In JNI, translate these to Java Exceptions or specific error objects.

### Smart Pointers
- `Box<T>`: Heap allocation.
- `Arc<T>`: Atomic Reference Counting (for sharing data across threads).
- `Mutex<T>` / `RwLock<T>`: Safe interior mutability for shared state.

---

## 3. Asynchronous Rust: The Reactive Core

### Futures and Tasks
Rust's `Future` is a state machine that represents a value that will eventually be available.
- **Poll-based**: Futures don't do anything until polled.
- **Executors**: (e.g., Tokio) poll futures to completion.

### Async/Await Syntax
```rust
async fn fetch_data() -> String {
    // asynchronous code here
}

// In a task
let result = fetch_data().await;
```

### Streams: Asynchronous Iterators
`Stream` is the async equivalent of `Iterator`. This is the foundation for a reactive approach (e.g., Kotlin `Flow`).
- `StreamExt::next()`: Get the next item.
- `StreamExt::map`, `filter`, `fold`.

---

## 4. JNI Interoperability with Robusta

`robusta` simplifies JNI by using procedural macros to generate boilerplate.

### Setup
Add to `Cargo.toml`:
```toml
[dependencies]
robusta_jni = "0.2"
```

### Basic Binding
```rust
use robusta_jni::bridge;

#[bridge]
mod jni {
    #[package(com.example.app)]
    pub struct NativeLib;

    impl NativeLib {
        pub fn stringMethod(env: &JNIEnv, _class: JClass, input: String) -> String {
            format!("Hello from Rust, {}!", input)
        }
    }
}
```

### Type Conversions
`robusta` handles automatic conversion for:
- Primitive types (`i32` <-> `int`, etc.)
- `String` <-> `java.lang.String`
- `Vec<T>` <-> `java.util.List`
- Custom classes (via `FromJavaValue`/`IntoJavaValue`)

---

## 5. The Reactive Approach: Bridging Rust to Kotlin

### Strategy: Async Rust to `CompletableFuture`
Since JNI calls are typically synchronous, a reactive wrapper should offload work to a Rust thread/runtime and return a future-like object to Kotlin.

1.  **Global Runtime**: Maintain a static `Tokio` runtime in Rust.
2.  **Spawning Tasks**: Use `runtime.spawn()` to run async tasks.
3.  **Returning Results**: 
    - For single values: Pass a `CompletableFuture` from Java to Rust, and complete it when the Rust task finishes.
    - For streams: Use a callback mechanism or a custom "Listener" class that Rust calls as new data arrives (mapping to Kotlin `Flow`).

### Example: Async Callback Pattern
**Kotlin:**
```kotlin
class NativeWrapper {
    external fun startAsyncOperation(callback: (Result) -> Unit)
}
```

**Rust (with robusta):**
```rust
impl NativeWrapper {
    pub fn startAsyncOperation(env: &JNIEnv, _obj: JObject, callback: JObject) {
        let runtime = get_runtime(); // static tokio runtime
        let callback_global = env.new_global_ref(callback).unwrap();

        runtime.spawn(async move {
            let result = do_something_async().await;
            // Use JNI call back to Java/Kotlin
            call_kotlin_callback(callback_global, result);
        });
    }
}
```

### Best Practices for Reactive Wrappers
- **Threading**: Never block the JNI thread. Use Rust channels to communicate between the JNI thread and the async runtime.
- **Memory Safety**: Be extremely careful with `GlobalRef` and JNI lifetimes when passing objects into background async tasks.
- **Backpressure**: When wrapping `Streams` into `Flows`, ensure the Rust side respects Java/Kotlin's consumption rate if possible, or implement buffering strategies.

---

## 6. Official Resources & Guidelines
- [The Rust Book](https://doc.rust-lang.org/book/)
- [Async Rust Book](https://rust-lang.github.io/async-book/)
- [Robusta JNI Documentation](https://docs.rs/robusta_jni/latest/robusta_jni/)
- [Tokio Documentation](https://tokio.rs/tokio/tutorial)
