# Spec: Multiplatform WebTransport Support (JS & WASM)

## Overview
This track involves refactoring the existing JVM-centric `kwtransport` library to a truly multiplatform architecture. The goal is to maximize code sharing in `commonMain` using the `expect`/`actual` pattern, allowing the upcoming JS and WASM targets to reuse the core API and logic. The JS and WASM implementations will leverage the browser's native WebTransport API, while the JVM implementation will continue to use the Robusta-based JNI bridge (UniFFI was discarded earlier).

## Functional Requirements
- **Common API:** Define a consistent, idiomatic Kotlin API in `commonMain` for WebTransport operations.
- **Platform Implementations:**
    - **JVM:** Refactor to implement the `actual` declarations using the existing Robusta/JNI bridge.
    - **JS/WASM:** Implement the `actual` declarations using the browser's native WebTransport API.
- **Connection Management:** Support opening, closing, and state tracking across all platforms.
- **Stream Handling:** Support for creating and accepting bidirectional and unidirectional streams.
- **Datagrams:** Support for sending and receiving unreliable datagrams.
- **Flow-based API:** Use Kotlin `Flow` for incoming streams and datagrams (e.g., `connection.incomingBiStreams: Flow<BiStream>`).
- **Error Handling:** Implement a unified exception hierarchy in `commonMain`.

## Non-Functional Requirements
- **Performance:** Maintain high performance on JVM (via JNI) and leverage native browser optimizations on JS/WASM.
- **Consistency:** Ensure identical API behavior across all supported platforms.
- **Testability:** Maximize test coverage in `commonTest` to ensure cross-platform correctness.

## Acceptance Criteria
- [ ] Core components (Connection, Stream, Datagram, Endpoint) are moved to `commonMain` as `expect` classes/interfaces.
- [ ] JVM implementation is refactored to fulfill `actual` declarations without regression.
- [ ] JS target is implemented and passes all relevant tests in a browser environment.
- [ ] WASM target is implemented and passes all relevant tests in a browser environment.
- [ ] `commonTest` contains the majority of the test suite, running on JVM, JS, and WASM.
- [ ] Incoming streams and datagrams are exposed via `Flow`.

## Out of Scope
- Android implementation (reserved for a future track).
- Integration tests against non-kwtransport WebTransport implementations.
