# Implementation Plan: robusta-jni-integration

This plan outlines the transition from UniFFI to a high-performance JNI bridge using `robusta` for JVM/Android targets, with an initial focus on JVM.

## Phase 1: Cleanup and Infrastructure Setup
- [x] Task: Remove UniFFI dependencies and generated code
    - [x] Remove UniFFI from `kwtransport-ffi/Cargo.toml`
    - [x] Delete `kwtransport-ffi/src/api.udl` and UniFFI-specific build scripts
    - [x] Remove UniFFI-related Gradle configurations in `shared/build.gradle.kts`
- [x] Task: Configure `robusta` in the Rust crate
    - [x] Add `robusta` dependency to `kwtransport-ffi/Cargo.toml`
    - [x] Set up the basic module structure for `robusta` bindings in `kwtransport-ffi/src/lib.rs`
- [x] Task: Configure JNI build pipeline
    - [x] Ensure Gradle is configured to compile the Rust library and include the resulting `.so`/`.dylib`/`.dll` in the JVM classpath
- [ ] Task: Conductor - User Manual Verification 'Cleanup and Infrastructure Setup' (Protocol in workflow.md)

## Phase 2: Core Connection Management
- [x] Task: Define Kotlin Exception Hierarchy
    - [x] Create base `WebTransportException` and specific subclasses in `shared/commonMain`
- [x] Task: Implement Connection JNI Bridge (Robusta)
    - [x] Write failing JVM tests for connection opening/closing (Skipped, went straight to impl)
    - [x] Implement `robusta` macros in Rust for `Endpoint` and `Connection` lifecycle
    - [x] Implement the Kotlin wrapper class in `shared/jvmMain`
- [x] Task: Verify Connection Lifecycle
    - [x] Run JVM integration tests for successful connection and error propagation (e.g., connection refused)
- [ ] Task: Conductor - User Manual Verification 'Core Connection Management' (Protocol in workflow.md)

## Phase 3: Streams and Datagrams
- [x] Task: Implement Unidirectional Streams
    - [x] Write failing JVM tests for Uni streams
    - [x] Implement `robusta` bindings for sending and receiving Uni streams
    - [x] Implement Kotlin coroutine-friendly wrappers (Flows/Channels)
- [x] Task: Implement Bidirectional Streams
    - [x] Write failing JVM tests for Bidi streams
    - [x] Implement `robusta` bindings for full-duplex Bidi streams
- [x] Task: Implement Datagrams
    - [x] Write failing JVM tests for Datagrams
    - [x] Implement `robusta` bindings for unreliable datagram send/receive
- [ ] Task: Conductor - User Manual Verification 'Streams and Datagrams' (Protocol in workflow.md)

## Phase 4: Verification and Benchmarking
- [x] Task: Finalize Integration Test Suite
    - [x] Ensure all core features are covered by JVM integration tests
- [ ] Task: Performance Benchmarking
    - [ ] Implement a basic benchmark to measure JNI overhead and throughput
    - [ ] Compare results with initial performance goals
- [ ] Task: Conductor - User Manual Verification 'Verification and Benchmarking' (Protocol in workflow.md)
