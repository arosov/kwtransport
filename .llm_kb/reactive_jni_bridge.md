# Reactive JNI Bridge: Architecture & Implementation

This document defines the architecture used in `kwtransport` to bridge Rust's asynchronous `wtransport` (Tokio-based) with Kotlin's coroutines without using blocking JNI calls or manual polling.

---

## 1. Core Problem
JNI calls are naturally synchronous. If a Kotlin thread calls a native function that awaits a network response, that thread is held hostage until Rust returns. In a high-concurrency environment, this leads to thread pool exhaustion and deadlocks.

## 2. The Solution: The Async Registry Pattern
Instead of blocking, we use a **Tag-and-Notify** system, similar to how modern OS kernels handle I/O (`io_uring`).

### The Flow:
1.  **Kotlin Side:**
    - Generate a unique `Long` ID.
    - Store a `CompletableDeferred<Long>` in a thread-safe `AsyncRegistry` keyed by that ID.
    - Call the native function and return to the caller immediately.
    - The Kotlin coroutine `suspends` on the deferred value.
2.  **Rust Side:**
    - Receive the ID.
    - `tokio::spawn` a background task to do the heavy lifting (network I/O).
    - The JNI thread returns to Kotlin immediately (non-blocking).
3.  **The Callback:**
    - When the Rust task finishes, it attaches the background thread to the `JavaVM`.
    - It calls a single, static method: `JniHelper.onNotify(id, result, error)`.
    - Rust detaches the thread.
4.  **Completion:**
    - Kotlin's `onNotify` looks up the ID in the `AsyncRegistry`.
    - It completes the deferred value, waking up the suspended coroutine.

---

## 3. Implementation Details

### Kotlin: `AsyncRegistry`
A `ConcurrentHashMap` stores the pending operations. This allows a single static entry point (`JniHelper`) to handle thousands of concurrent requests.

### Rust: Thread Attachment & Safety
Since background threads in Rust aren't known to the JVM, they must be manually attached before calling back:
```rust
let vm = JAVA_VM.get().expect("JavaVM not initialized");
let env = vm.attach_current_thread().expect("Failed to attach thread");
// Call Kotlin static method via robusta...
```

### Rust: The `PtrSend` Hack
Rust pointers (`*const T`) do not implement `Send` by default, meaning they cannot be moved into a `tokio::spawn` block. We use a wrapper to bypass this for handles that we know are safe to share:
```rust
pub struct PtrSend(pub *const NativeEndpoint);
unsafe impl Send for PtrSend {}
```

---

## 4. Why This Pattern?
1.  **Zero Polling:** Neither side wastes CPU cycles checking for completion.
2.  **Minimal GlobalRefs:** We only maintain a reference to the `JavaVM` and a few static classes, rather than a `GlobalRef` for every single listener object.
3.  **Unified Reactive Entry Point:** The `onNotify` signature can easily be extended to support `Flows` (Multi-shot) by passing a `Channel` ID instead of a `Deferred` ID.

---

## 5. Known Constraints
- **Method Signatures:** Use standard types (`String`, `i64`, `Vec<u8>`) in the bridge. Avoid `Option<T>` or complex generic wrappers directly in the `robusta` macro as they often lead to JNI signature mismatches.
- **Exception Handling:** In `unchecked` mode, if a JNI exception is pending, Rust code that returns a `String` or `JObject` will panic. Always check `env.exception_check()` or return a primitive `0`/`null` if an error has been thrown.
