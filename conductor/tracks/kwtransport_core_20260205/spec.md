# Specification: Core KwTransport & Reliable Streams

## Goal
Establish the foundational `KwTransport` class structure and implement support for reliable streams (uni-directional and bi-directional). Initially, UniFFI was considered to bridge to the underlying Rust `wtransport` implementation, but it was discarded in favor of a direct Robusta JNI implementation to meet performance requirements.

## Core Components
1.  **Rust Backend (kwtransport-ffi):**
    -   Expose `Endpoint` creation and management via Robusta JNI.
    -   Expose methods to open and accept reliable streams.
    -   Expose stream reading and writing operations.
2.  **Kotlin Wrapper (Shared Module):**
    -   `KwTransport`: The main entry point for the library.
    -   `KwConnection`: Represents an active WebTransport session.
    -   `KwSendStream` / `KwRecvStream`: Wrappers for reliable streams.
    -   Integrate with Kotlin Coroutines for async operations.

## Requirements
-   **Idiomatic Kotlin:** Use `suspend` functions and standard types.
-   **Zero-Copy (where feasible):** Use direct ByteBuffers or efficiently bridged arrays.
-   **Error Handling:** Map Rust errors to `KwTransportException`.
-   **Testing:** Basic integration tests to verify stream open/read/write.

## Detailed Flow
1.  Initialize `KwTransport` (which initializes the Rust runtime).
2.  Establish a connection to a server (using a test server).
3.  Open a bi-directional stream.
4.  Write data to the stream.
5.  Read data from the stream.
6.  Close the stream.
