# Generic KMP Project Conventions

This document outlines the key conventions and architectural guidelines for this project. You should adhere to these principles when generating or modifying code.

## 1. Project Structure & Technology

* **Type:** Kotlin Multiplatform (KMP) project.
* **Supported targets:** JVM, Android, WASM
* **Goal:** Maximize code sharing across platforms (e.g., Android, JVM, WASM) using `commonMain`, `commonTest`, and platform-specific source sets (`androidMain`, `iosMain`, `jvmMain`, `wasmJsMain`, etc.).
* **Build System:** Gradle with Kotlin DSL (`build.gradle.kts`). Use Gradle Version Catalogs (`libs.versions.toml`) for dependency management.

## 2. Version Control & CI/CD

* **Hosting:** The codebase is hosted on **GitHub**.
* **CI/CD:** All Continuous Integration and Continuous Deployment pipelines **must** be implemented using **GitHub Actions**.
    * Workflows should be defined in the `.github/workflows/` directory.
    * Common tasks include building, linting, running tests (unit and integration), and potentially deploying artifacts.

## 3. Cloud Infrastructure

* **Provider:** **Google Cloud Platform (GCP)** is the preferred cloud provider if not otherwise specified.
* **Cost Optimization:** Prioritize using services within the **GCP Free Tier** whenever feasible.
    * Before suggesting or implementing a GCP service, consider its free tier limits (e.g., Cloud Functions invocation counts, Firestore read/writes, Cloud Run CPU-seconds, Cloud Storage limits).
    * If the Free Tier is insufficient, select cost-effective managed services. Document the reasons if non-free tier usage is necessary.

## 4. Architecture: Clean Architecture

* **Core Principle:** The project structure and dependency flow **must** adhere to **Clean Architecture** principles.
* **Layers:** Maintain distinct layers:
    * **Domain:** Core business logic, entities, use cases (interactors), and repository interfaces. **Crucially, this layer must have ZERO external dependencies.** No frameworks, no platform specifics, no infrastructure details (database, network, etc.). It should be pure Kotlin, primarily residing in `commonMain`.
    * **Application/Use Cases:** (Often part of Domain or a thin layer above it) Orchestrates use cases, depends only on the Domain layer.
    * **Infrastructure/Data:** Implementation details. Includes data sources (network clients, databases, device storage), framework implementations, etc. Implements interfaces defined in the Domain layer. Depends on Domain.
    * **Presentation/UI:** Platform-specific UI and presentation logic (e.g., Android Activities/Fragments/Composables, iOS ViewControllers/SwiftUI Views, Web components). Depends on Application/Use Cases or ViewModels derived from them.
* **Dependency Rule:** Dependencies flow inwards. Outer layers (UI, Infrastructure) depend on inner layers (Domain). The Domain layer depends on nothing external to itself.

## 5. Kotlin Code Style & Organization

* **Style Guide:** Follow the official Kotlin coding conventions ([https://kotlinlang.org/docs/coding-conventions.html](https://kotlinlang.org/docs/coding-conventions.html)). If targeting Android, also adhere to the Android Kotlin Style Guide.
* **Feature Modules:**
    * Most distinct features **should be isolated** into their own **Gradle modules** under the top-level `features/` directory.
    * These feature modules should reside under a top-level `features/` directory.
    * **Internal Architecture:** Each feature module **should itself** strive to implement Clean Architecture internally (e.g., `features/authentication/domain`, `features/authentication/data`, `features/authentication/presentation`). The scope might be smaller, but the layering and dependency rules still apply within the module.
    * **Dependencies:** Feature modules can depend on core/libraries modules (e.g., a `core/common`, `core/domain`, `libraries/randomtool` module) but should generally avoid direct dependencies on *other* feature modules. Use shared interfaces or event mechanisms defined in common modules for inter-feature communication if necessary.
* **Immutability:** Prefer immutable data structures (`val`, `listOf`, `mapOf`, immutable data classes) where possible.
* **Coroutines:** Use Kotlin Coroutines for asynchronous operations. Utilize structured concurrency.
* **Nullability:** Leverage Kotlin's null-safety features correctly. Avoid unnecessary nullable types and the non-null asserted (`!!`) operator.

## 6. Dependencies

* **KMP Compatibility:** Ensure added dependencies are KMP-compatible and account for supported targets or provide necessary `expect`/`actual` implementations.
* **Management:** Use Gradle Version Catalogs (`libs.versions.toml` under the `gradle` directory) to define and manage dependency versions centrally.
* **UUID:** When using UUID objects, prefer kotlinx.uuid over other java options in multiplatform context.

## 7. Testing

* **Unit Tests:** Write unit tests for business logic (Domain layer, Use Cases) and Presentation logic (ViewModels) when requested. Place common tests in `commonTest`. These should be fast and have no external dependencies (use fakes or mocks).
* **Integration Tests:** Write integration tests for interactions between layers (e.g., Use Case interacting with a repository implementation) when requested.
* **Platform-Specific Tests:** UI tests (Espresso, XCUITest) and tests requiring platform APIs belong in platform-specific test source sets (e.g., `androidTest`, `wasmTest`).
* **Coverage:** Aim for high test coverage, especially in the Domain layer.

## 8. Documentation

* **KDoc:** Write KDoc comments for all public APIs (classes, functions, properties) and complex internal logic.

## 9. Specific Instructions

* **Consult this File:** Always refer to these conventions before generating or modifying code.
* **Clarify Ambiguity:** If a request is unclear or conflicts with these conventions, ask for clarification.
* **Prioritize Conventions:** Adherence to these architectural and structural rules is paramount, even if it requires more verbose code than a simpler, less structured approach.
* **Explain Choices:** When making significant architectural decisions (e.g., adding a new module, choosing a specific GCP service), briefly explain the reasoning in the context of these conventions.

# Gradle module conventions

The main application is in the `:composeApp` module.

To run it from CLI, use `./gradlew :composeApp:run`

To run tests associated with a specific module, use the `test` task instead, such as `./gradlew :composeApp:test`.

Always use the `clean<Target>Test` task before running tests to ensure results are recomputed (e.g., `./gradlew :module:cleanJvmTest :module:jvmTest`).

### Target-Specific Commands

| Target | Test Command | Compile Command |

| :--- | :--- | :--- |

| **JVM** | `./gradlew :module:cleanJvmTest :module:jvmTest` | `./gradlew :module:compileKotlinJvm` |

| **WASM** | `./gradlew :module:cleanWasmJsTest :module:wasmJsTest` | `./gradlew :module:compileKotlinWasmJs` |

| **Android** | `./gradlew :module:cleanAndroidTest :module:connectedAndroidTest` | `./gradlew :module:compileDebugKotlinAndroid` |

Some modules can be under one or several directories. This project contains a `logging` module under the `features` directory.

When that happens, the module identifier looks like this `:features:logging`. So to build it, run the `build` task of that module

with `./gradlew :features:logging:build`.



# Project specific informations

This project specifically is named kwtransport. It aims to provide a high
performance wrapper to Rust's wtransport crate with Robusta.

## Implementation & SourceSet Management

The project uses two fundamentally different underlying implementations:
* **JVM & Android:** These targets are backed by the **Rust `wtransport` crate** via a Robusta JNI bridge.
* **JS & WASM:** These targets are backed by the **browser's native WebTransport API**.

**Impact on SourceSets:**
* Logic and configuration specific to the Rust implementation (e.g., `QuicConfig`, `CustomDnsResolver`, low-level tuning) resides in `jvmAndroidMain`.
* `commonMain` contains only the intersection of capabilities supported by both the Rust crate and the browser API.
* Avoid moving native-specific or Rust-specific logic to `commonMain` to keep web targets lean and prevent non-functional API exposure.

The jvm target is a priority in part due to testing being faster.

When dealing with jvmTest, always use the target cleanJvmTest before running the tests with jvmTest,
otherwise the test results won't be recomputed.
