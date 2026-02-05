# Robustness Testing Strategy for Kotlin wtransport Wrapper

To maximize robustness for a Kotlin wrapper over a Rust networking crate (wtransport), this strategy goes beyond "happy-path" unit tests, focusing on the FFI/JNI boundary, transport state, and async concurrency.

## 1. FFI Boundary Tests (Highest Priority)
**Risk:** Undefined behavior across JNI/FFI (Use-after-free, Double-free, Leaks).

### A. Memory & Lifetime Stress
*   **Test for:** Use-after-free, Double-free, Leaked handles, Kotlin GC vs Rust ownership.
*   **Tests:**
    *   Repeated create → use → close cycles (10k–100k iterations).
    *   Close called twice, from different threads, or during active I/O.
    *   Kotlin objects going out of scope without explicit close (finalizer checks).
*   **Technique:**
    ```kotlin
    repeat(50_000) {
        val client = WTransportClient(...)
        client.connect()
        client.send(...)
        client.close()
    }
    ```
*   **Flags:** `ASAN + UBSAN` (Rust), `-Xcheck:jni`, `-XX:+HeapDumpOnOutOfMemoryError`.

### B. Thread Affinity Violations
*   **Risk:** Users misusing threading with thread-bound resources (Tokio).
*   **Tests:**
    *   Create on Thread A → Use on Thread B → Close on Thread C.

## 2. Error Injection / Fault Testing

### A. Rust-Side Fault Injection
*   Expose a `#[cfg(test)]` Rust API: `pub fn force_error(kind: ErrorKind)`.
*   **Inject:** Handshake failure, reset, timeout, malformed frames.
*   **Assert:** Kotlin receives correct exception, no crash, no leaks.

### B. Invalid Input Fuzzing (Kotlin-Side)
*   **Fuzz:** Byte arrays, URLs, ALPN, headers, stream IDs.
*   **Goal:** Rust rejects invalid input; Kotlin never segfaults.
*   **Tool:** Property-based testing (Kotest `forAll`).

## 3. Concurrency & Ordering Tests
**Risk:** Stateful + Async = Race Conditions.

### A. High-Concurrency Tests
*   Open 100+ streams concurrently.
*   Send/close in random order.
*   Close connection mid-flight.
*   Use `Dispatchers.Default`, `Dispatchers.IO`, virtual threads.

### B. Cancellation Tests
*   Cancel coroutine mid-send / during handshake.
*   **Assert:** Native resources released, no deadlocks, no orphaned Rust tasks.

## 4. Protocol-Level Black-Box Tests

### A. Interop Tests
*   Test against pure Rust peer, Chrome (WebTransport), other QUIC impls.

### B. Network Adversity Simulation
*   **Simulate:** Packet loss, latency, jitter, drops (using `tc netem` or Docker).
*   **Focus:** Reconnect behavior, error propagation, JVM stability.

## 5. Resource Accounting Tests

### A. Leak Detection
*   Expose Rust counters: `static ACTIVE_CONNECTIONS: AtomicUsize`.
*   **Test:** Count returns to zero after suite.

### B. File Descriptor Exhaustion
*   Artificially lower limits (`ulimit -n 64`).
*   **Ensure:** Graceful failure, no crash.

## 6. ABI & Packaging Tests

### A. Binary Compatibility
*   Test multiple JVMs (8, 11, 17, 21) and OS/arch combos.

### B. Load/Unload Cycles
*   Load JNI → use → unload → reload.

## 7. Chaos & Long-Running Tests (Nightly)
*   24h connection churn, random disconnects, GC pressure.

## 8. Essential Tooling
*   **Rust:** `cargo miri`, ASAN/UBSAN, `loom`, `tracing`.
*   **JVM:** `-Xcheck:jni`, `-XX:+VerifyBeforeGC`, Heap dumps.

## 9. Minimal "Robustness Baseline" Checklist
*   [ ] Stress create/use/close loops
*   [ ] Thread misuse tests
*   [ ] Kotlin coroutine cancellation tests
*   [ ] Rust fault injection
*   [ ] Leak counters validated
*   [ ] Fuzzed input tests
