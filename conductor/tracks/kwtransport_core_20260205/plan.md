# Implementation Plan - Core KwTransport & Reliable Streams

## Phase 1: Rust FFI Foundation [checkpoint: edb7d34]
This phase focuses on exposing the necessary `wtransport` functionality via Robusta JNI.

- [x] Task: Define JNI interface for `Endpoint` and `Connection` in Rust. [3593ec3]
    - [ ] Implement `Endpoint` creation logic in Rust using Robusta.
    - [ ] Implement `connect` method returning a `Connection`.
- [x] Task: Define JNI interface for Reliable Streams. [3ad56c4]
    - [ ] Add `open_uni_stream` and `accept_uni_stream` to `Connection`.
    - [ ] Add `open_bi_stream` and `accept_bi_stream` to `Connection`.
    - [ ] Define `SendStream` and `RecvStream` interfaces with `read` and `write` methods.
- [x] Task: Implement Stream logic in Rust. [619f31a]
    - [ ] Implement the stream wrappers in `kwtransport-ffi/src/lib.rs`.
    - [ ] Ensure proper async bridging with Tokio.
- [ ] Task: Conductor - User Manual Verification 'Phase 1' (Protocol in workflow.md)

## Phase 2: Kotlin Wrapper Structure
This phase focuses on creating the Kotlin-side structure that consumes the JNI code.

- [~] Task: Set up Kotlin Shared Module for JNI.
    - [ ] Ensure Rust compilation is correctly configured in `kwtransport/build.gradle.kts`.
    - [ ] Verify JNI calls work.
- [ ] Task: Implement `KwTransport` entry point.
    - [ ] Create `KwTransport` object/class.
    - [ ] Add initialization logic.
- [ ] Task: Implement `KwConnection` wrapper.
    - [ ] Create `KwConnection` class wrapping the Rust `Connection`.
    - [ ] Add `connect` suspend function.
- [ ] Task: Conductor - User Manual Verification 'Phase 2' (Protocol in workflow.md)

## Phase 3: Reliable Streams Implementation
This phase connects the Kotlin stream wrappers to the Rust streams.

- [ ] Task: Implement `KwSendStream` and `KwRecvStream`.
    - [ ] Create Kotlin classes wrapping the Rust stream handles.
    - [ ] Implement `write` (suspend) for `KwSendStream`.
    - [ ] Implement `read` (suspend) for `KwRecvStream`.
- [ ] Task: Integration Test - Bi-directional Stream.
    - [ ] Create a test in `kwtransport/src/commonTest` that connects to a local test server.
    - [ ] Verify sending and receiving data works.
- [ ] Task: Conductor - User Manual Verification 'Phase 3' (Protocol in workflow.md)
