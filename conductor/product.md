# Initial Concept

## Overview
kwtransport is a high-performance Kotlin Multiplatform (KMP) wrapper for the Rust `wtransport` crate, utilizing UniFFI to bridge the two ecosystems. The primary goal is to provide a seamless, idiomatic Kotlin API for WebTransport that runs efficiently across Android, JVM, and WASM targets. By leveraging the robustness and speed of Rust's `wtransport`, kwtransport aims to offer a superior networking solution for KMP developers.

## Target Audience
- **Kotlin Multiplatform Developers:** Specifically those who require robust and efficient WebTransport support within their cross-platform applications.

## Core Goals
- **Idiomatic Kotlin API:** Deliver a natural and intuitive API experience for Kotlin developers, abstracting away the complexities of the underlying Rust FFI.
- **Cross-Platform Support:** Ensure consistent and reliable WebTransport functionality across all supported KMP targets (Android, JVM, WASM).
- **High Performance FFI:** Specifically utilize UniFFI's JNI backend (rather than JNA) to ensure maximum performance and minimal overhead on JVM-based targets.

## Key Features
- **Transport Modes:** Full support for both reliable streams (for ordered, guaranteed delivery) and unreliable datagrams (for low-latency, real-time data).
- **Performance:** Minimized overhead to maintain the high throughput and low latency characteristics of the underlying Rust implementation.

## Non-Functional Requirements
- **Performance:** Achieve high throughput and low latency, comparable to native Rust implementations.
- **Consistency:** Guarantee cross-platform consistency in both runtime behavior and API surface.
- **Maintainability:** Prioritize ease of debugging and provide clear, actionable error reporting for developers.
