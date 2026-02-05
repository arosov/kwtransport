# Product Guidelines

## Prose Style
- **Technical & Precise:** Documentation should prioritize technical accuracy and detailed specifications.
- **Focus:** Clear, unambiguous API documentation is paramount.
- **Tone:** Professional, objective, and developer-focused. Avoid excessive marketing fluff; focus on the "what" and "why" of technical decisions.

## Visual Identity
- **Standard KMP/JetBrains:** Adhere to the established design language of the Kotlin and JetBrains ecosystem.
- **Consistency:** Use standard color palettes and formatting conventions found in official Kotlin documentation to ensure the library feels like a natural part of the ecosystem.

## API Design Principles
- **Idiomatic Kotlin:**
    - Leverage Kotlin's strengths: Coroutines, Flows, and standard library types.
    - **Abstraction:** Completely hide the underlying Rust/FFI complexity. The user should never see `UnsafePointer` or raw handle types.
- **Zero-Copy Optimization:**
    - Prioritize zero-copy operations for data transfer between Rust and Kotlin.
    - **Performance:** Accept slightly more complex internal implementation (or even API surface if absolutely necessary) to avoid unnecessary data copying, ensuring the wrapper adds minimal overhead.

## Error Handling
- **Kotlin Exceptions:**
    - **Mapping:** All errors originating from the Rust layer must be mapped to appropriate, standard Kotlin exceptions (e.g., `IOException`, `IllegalArgumentException`) or custom exception subclasses where specific semantic meaning is required.
    - **Clarity:** Exceptions should contain clear messages that help the developer understand the root cause, potentially including error codes from the underlying `wtransport` crate.
