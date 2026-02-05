# Plan: WASM Integration Test Setup

## Phase 1: API Alignment and Certificate Support
Update the common and browser APIs to support certificate hashes.

- [x] Task: Update `expect fun createClientEndpoint` in `commonMain` to include `certificateHashes: List<String> = emptyList()`. [2b29762]
- [x] Task: Implement `certificateHashes` support in `jsMain` and `wasmJsMain` using the browser's `WebTransportOptions`. [2a5a12e]
- [x] Task: Update `Endpoint.connect` in `jsMain` and `wasmJsMain` to pass the options to the `JsWebTransport` constructor. [2a5a12e]
- [ ] Task: Conductor - User Manual Verification 'API Alignment'

## Phase 2: JVM Test Server and Fixed Certificate
Create the server component and define the fixed certificate.

- [ ] Task: Define a fixed self-signed certificate and its SHA-256 hash as constants in a new `TestCertificate` file.
- [ ] Task: Implement a standalone JVM main function that starts a `KwTransport` server using the fixed certificate and echos received data.

## Phase 3: Gradle Automation and Integration Test
Connect the pieces and run the test.

- [ ] Task: Implement a WASM-specific integration test that connects to the local server using the fixed fingerprint.
- [ ] Task: Configure Gradle to automate the starting and stopping of the JVM server process during the `wasmJsTest` task.
- [ ] Task: Conductor - User Manual Verification 'Full WASM Integration'
