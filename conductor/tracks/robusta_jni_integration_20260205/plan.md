# Implementation Plan: robusta-jni-integration

This plan outlines the transition from UniFFI to a high-performance JNI bridge using `robusta` for JVM/Android targets, with an initial focus on JVM.

## Phase 1: Cleanup and Infrastructure Setup
- [ ] Task: Remove UniFFI dependencies and generated code
    - [ ] Remove UniFFI from `kwtransport-ffi/Cargo.toml`
    - [ ] Delete `kwtransport-ffi/src/api.udl` and UniFFI-specific build scripts
    - [ ] Remove UniFFI-related Gradle configurations in `shared/build.gradle.kts`
- [ ] Task: Configure `robusta` in the Rust crate
    - [ ] Add `robusta` dependency to `kwtransport-ffi/Cargo.toml`
    - [ ] Set up the basic module structure for `robusta` bindings in `kwtransport-ffi/src/lib.rs`
- [ ] Task: Configure JNI build pipeline
    - [ ] Ensure Gradle is configured to compile the Rust library and include the resulting `.so`/`.dylib`/`.dll` in the JVM classpath
- [ ] Task: Conductor - User Manual Verification 'Cleanup and Infrastructure Setup' (Protocol in workflow.md)

## Phase 2: Core Connection Management
- [ ] Task: Define Kotlin Exception Hierarchy
    - [ ] Create base `WebTransportException` and specific subclasses in `shared/commonMain`
- [ ] Task: Implement Connection JNI Bridge (Robusta)
    - [ ] Write failing JVM tests for connection opening/closing
    - [ ] Implement `robusta` macros in Rust for `Endpoint` and `Connection` lifecycle
    - [ ] Implement the Kotlin wrapper class in `shared/jvmMain`
- [ ] Task: Verify Connection Lifecycle
    - [ ] Run JVM integration tests for successful connection and error propagation (e.g., connection refused)
- [ ] Task: Conductor - User Manual Verification 'Core Connection Management' (Protocol in workflow.md)

## Phase 3: Streams and Datagrams
- [ ] Task: Implement Unidirectional Streams
    - [ ] Write failing JVM tests for Uni streams
    - [ ] Implement `robusta` bindings for sending and receiving Uni streams
    - [ ] Implement Kotlin coroutine-friendly wrappers (Flows/Channels)
- [ ] Task: Implement Bidirectional Streams
    - [ ] Write failing JVM tests for Bidi streams
    - [ ] Implement `robusta` bindings for full-duplex Bidi streams
- [ ] Task: Implement Datagrams
    - [ ] Write failing JVM tests for Datagrams
    - [ ] Implement `robusta` bindings for unreliable datagram send/receive
- [ ] Task: Conductor - User Manual Verification 'Streams and Datagrams' (Protocol in workflow.md)

## Phase 4: Verification and Benchmarking
- [ ] Task: Finalize Integration Test Suite
    - [ ] Ensure all core features are covered by JVM integration tests
- [ ] Task: Performance Benchmarking
    - [ ] Implement a basic benchmark to measure JNI overhead and throughput
    - [ ] Compare results with initial performance goals
- [ ] Task: Conductor - User Manual Verification 'Verification and Benchmarking' (Protocol in workflow.md)
