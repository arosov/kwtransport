# Plan: Multiplatform WebTransport Support (JS & WASM)

Refactor `kwtransport` to a multiplatform architecture, maximizing `commonMain` code and implementing JS/WASM targets using native browser WebTransport.

## Phase 1: API Extraction and Commonization [checkpoint: jvmAndroidMain]
Extract the core API from the JVM implementation into `commonMain` using `expect`/`actual`.

- [x] Task: Define `expect` classes and interfaces in `commonMain` for `Endpoint`, `Connection`, `SendStream`, `ReceiveStream`, and `Datagram`.
- [x] Task: Implement `actual` declarations in `jvmMain` (now `jvmAndroidMain`) by wrapping the existing Robusta-based JNI classes (UniFFI was discarded earlier).
- [x] Task: Refactor existing JVM tests to `commonTest` where possible, using `expect`/`actual` for test setup (e.g., certificate generation).
- [x] Task: Update the API to use `Flow` for incoming sessions, streams, and datagrams in `commonMain`.
- [x] Task: Conductor - User Manual Verification 'API Extraction and Commonization' (Protocol in workflow.md)

## Phase 2: JS and WASM Implementation
Implement the `actual` declarations for JS and WASM targets using the browser's native WebTransport API.

- [x] Task: Implement `WebTransport` bindings and `actual` classes in `jsMain` and `wasmJsMain`.
- [x] Task: Map browser `ReadableStream` and `WritableStream` to the common `ReceiveStream` and `SendStream` interfaces.
- [ ] Task: Implement datagram support using the browser's `datagrams` property. (Partial: JVM implemented, JS/WASM pending)
- [x] Task: Ensure proper error mapping from browser exceptions to the common exception hierarchy.
- [x] Task: Conductor - User Manual Verification 'JS and WASM Implementation' (Protocol in workflow.md)

## Phase 3: Multiplatform Testing and Verification
Enable and verify tests across all targets.

- [x] Task: Configure Gradle to run `commonTest` on JVM, JS (Karma/Playwright), and WASM targets.
- [x] Task: Implement platform-specific test helpers in `jsTest` and `wasmJsTest` for connecting to a test server.
- [x] Task: Run the full test suite on all platforms and fix any platform-specific discrepancies.
- [x] Task: Conductor - User Manual Verification 'Multiplatform Testing and Verification' (Protocol in workflow.md)