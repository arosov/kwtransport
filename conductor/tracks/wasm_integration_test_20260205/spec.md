# Spec: WASM Integration Test Setup

## Overview
Enable full integration testing for the WASM target by setting up a dedicated JVM-based test server. This involves implementing support for server certificate fingerprints in the WASM/JS client and automating the server's lifecycle via Gradle.

## Functional Requirements
- **Certificate Support:** Align the `createClientEndpoint` API in `commonMain` to support `certificateHashes` across all platforms.
- **Fixed Certificate:** Use a hardcoded self-signed certificate (PEM string) and its pre-calculated SHA-256 fingerprint for testing.
- **JVM Test Server:** Implement a standalone echo server in the `jvmTest` source set that listens on a fixed port (e.g., 4433).
- **Gradle Automation:** Create Gradle tasks to spawn the JVM server before WASM tests and terminate it afterward.
- **Integration Test:** Implement a WASM-based test that connects to the local JVM server and performs a bidirectional data exchange.

## Acceptance Criteria
- [ ] `createClientEndpoint` supports certificate fingerprints on JS/WASM.
- [ ] JVM echo server can be started/stopped via Gradle.
- [ ] WASM integration test successfully performs a round-trip data exchange in a headless browser.
