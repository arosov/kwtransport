# kwtransport: High-Performance WebTransport for Kotlin Multiplatform

`kwtransport` is a Kotlin Multiplatform library that provides a high-performance wrapper around the `wtransport` Rust crate, bringing WebTransport capabilities to your Kotlin applications across JVM, Android, and WebAssembly (WASM) targets. It aims to offer an idiomatic and robust Kotlin API for establishing WebTransport connections, managing streams, and handling datagrams with efficiency and ease.

> [!IMPORTANT]
> **Disclaimer:** This project is almost entirely **LLM-generated** (via Gemini). The author has **no knowledge of Rust** and relied on AI to bridge the gap between Kotlin and the underlying Rust implementation. While it is functional and tested, there may be unidiomatic Rust code or performance pitfalls.
>
> **Contributions are highly welcome!** If you are a Rust expert or a Kotlin Multiplatform enthusiast, your help in refining the FFI bridge, improving memory management, or expanding target support would be greatly appreciated.

## Features

*   **Kotlin Multiplatform:** Write once, run on JVM, Android, and WASM.
*   **High Performance:** Leverages the battle-tested `wtransport` Rust crate for underlying WebTransport protocol implementation.
*   **Asynchronous API:** Built with Kotlin Coroutines and `Flow` for modern, non-blocking network operations.
*   **Clean Architecture:** Designed with clear separation of concerns, making it easy to integrate into your projects.
*   **Reliable Stream & Datagram Handling:** Provides intuitive APIs for unidirectional, bidirectional streams, and unreliable datagrams.
*   **TLS Certificate Management:** Integrated handling for self-signed and trusted TLS certificates.

## Getting Started

To get started with `kwtransport`, ensure you have Java (JDK 17 or higher) and a recent version of the Kotlin Multiplatform plugin for Gradle installed.

### Building the Project

To build the entire project:

```bash
./gradlew build
```

### Running the Sample Application

The `composeApp` module contains a sample application demonstrating `kwtransport` usage. To run it on the JVM:

```bash
./gradlew :composeApp:run
```

### Running the CLI Chat Application

The `cli-chat` module provides a simple command-line chat application to demonstrate basic client-server communication using WebTransport streams.

1.  **Build the distribution (run once, or after code changes):**

    ```bash
    ./gradlew :cli-chat:installDist
    ```

2.  **Run the server:**
    Open a terminal and execute:

    ```bash
    ./cli-chat/build/install/cli-chat/bin/cli-chat -s
    ```

3.  **Run the client (in a new terminal):**

    ```bash
    ./cli-chat/build/install/cli-chat/bin/cli-chat
    ```

    Type `exit` to close a chat session.


### Running Tests

To run JVM tests for the `shared` module:

```bash
./gradlew :shared:cleanJvmTest :shared:jvmTest
```

For other modules or specific targets, replace `:shared` and `:jvmTest` as per the project's [Gradle module conventions](AGENTS.md#gradle-module-conventions).

## API Overview

The core of the `kwtransport` API resides in the `shared` module, primarily within the `ovh.devcraft.kwtransport` package. Key classes include:

*   `Endpoint`: The entry point for creating client and server WebTransport endpoints.
*   `Connection`: Represents an established WebTransport connection, allowing the opening and accepting of streams and sending/receiving datagrams.
*   `SendStream`: For sending data over a WebTransport stream.
*   `RecvStream`: For receiving data from a WebTransport stream, including a `Flow<ByteArray>` API for convenient consumption.
*   `Certificate`: Utility for managing TLS certificates.
*   `KwTransportException` and subclasses: Comprehensive exception hierarchy for robust error handling.

For detailed code examples demonstrating how to use these components, please see the [**Usage Examples documentation**](docs/examples.md).

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

## Credits

`kwtransport` is built upon the excellent work of the `wtransport` Rust crate, an implementation of the WebTransport (over HTTP3) protocol.

**`wtransport` Author:** Biagio Festa
**`wtransport` Repository:** [https://github.com/BiagioFesta/wtransport](https://github.com/BiagioFesta/wtransport)

---
**MIT License**

Copyright (c) 2026 Your Name or Organization

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
