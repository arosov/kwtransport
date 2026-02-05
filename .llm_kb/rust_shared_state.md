# Rust Shared-State Concurrency Knowledge Base

This document summarizes best practices for managing shared state and object lifetimes in concurrent Rust environments, based on the Rust Book.

## 1. Atomic Reference Counting (`Arc<T>`)

`Arc<T>` stands for "atomically reference-counted." It allows multiple threads to share ownership of an object. Unlike `Rc<T>`, which is not thread-safe, `Arc<T>` uses atomic operations to manage the reference count, making it safe to use across thread boundaries.

- **Usage:** Use `Arc::clone(&handle)` to create a new pointer to the same data.
- **Lifetime:** The data is only dropped when the last `Arc` pointer is dropped.
- **Cost:** Atomic operations have a small performance overhead compared to non-atomic ones.

## 2. Mutability with `Mutex<T>`

`Arc<T>` by itself only provides immutable access to the underlying data. To allow mutation, `Arc<T>` is often paired with `Mutex<T>`.

- **Interior Mutability:** `Mutex<T>` provides interior mutability, allowing data to be changed even if the `Arc` pointer is immutable.
- **Locking:** You must call `.lock()` to gain access to the data. This returns a `MutexGuard` which automatically releases the lock when it goes out of scope.

## 3. Best Practices for JNI and Background Tasks

In a JNI environment where Kotlin calls can initiate background Rust tasks (e.g., using `tokio::spawn`), the following pattern is essential to prevent Use-After-Free (UAF) errors:

1.  **Wrap Native Objects in `Arc`:** Instead of passing raw pointers (`Box::into_raw`), wrap the native struct in an `Arc`.
2.  **Clone for Background Tasks:** Before spawning an asynchronous task, clone the `Arc`. Move the clone into the task.
3.  **Kotlin Handle:** Store a raw pointer to the `Arc` itself in Kotlin. When Kotlin calls `close()`, decrement the reference count by dropping the `Arc` handle.
4.  **Safety:** The background task will keep the object alive even if Kotlin has "closed" its handle, preventing SIGSEGV.
