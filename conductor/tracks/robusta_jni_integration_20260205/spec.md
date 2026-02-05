# Track Specification: robusta-jni-integration

## Overview
This track involves transitioning from the UniFFI-based bridge to a high-performance JNI integration using `robusta` for JVM and Android targets. This shift is motivated by the requirement for JNI (over UniFFI's default JNA) to achieve maximum performance and lower overhead. The track will also involve removing UniFFI and preparing for a different WASM strategy (e.g., `wasm-bindgen`).

## Functional Requirements
- **Core WebTransport API:** Implement JNI bindings for:
    - Connection management (open/close).
    - Unidirectional streams (send/receive).
    - Bidirectional streams (full duplex).
    - Datagrams (unreliable low-latency).
- **Error Handling:** Map Rust `wtransport` errors to a custom Kotlin `Exception` hierarchy thrown across the JNI boundary.
- **JVM Priority:** Focus the initial implementation and verification on the JVM (Desktop) target.

## Non-Functional Requirements
- **Performance:** Ensure significantly lower overhead compared to JNA-based alternatives.
- **Idiomatic Kotlin:** Provide a clean, coroutine-friendly Kotlin API wrapping the JNI calls.
- **Maintainability:** Clear separation between JNI boilerplate and business logic using `robusta` macros.

## Acceptance Criteria
- [ ] UniFFI dependencies and generated code removed.
- [ ] `robusta` configured and integrated into the Rust crate.
- [ ] Core WebTransport features (Connections, Streams, Datagrams) functional on JVM via JNI.
- [ ] Integration tests pass on the JVM target.
- [ ] Exception mapping correctly propagates errors from Rust to Kotlin.
- [ ] Performance benchmarks recorded for the new bridge.

## Out of Scope
- **WASM Implementation:** The WASM target implementation is deferred to a future track.
- **Advanced TLS Configuration:** Complex custom TLS certificate handling beyond basic requirements is deferred.
