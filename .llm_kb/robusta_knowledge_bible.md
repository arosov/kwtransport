# The Robusta JNI Knowledge Bible

**Robusta JNI** is a Rust library that facilitates Easy Interop between Rust and Java. It uses procedural macros to generate JNI-compatible code, handling the boilerplate of type conversions and method signatures.

## 1. Core Concept: The Bridge

The heart of `robusta` is the `#[bridge]` attribute. It must be applied to a Rust module. This signals `robusta` to analyze the module's contents and generate the necessary JNI extern functions and trait implementations.

```rust
use robusta_jni::bridge;

#[bridge]
mod jni {
    // ... struct and impl definitions ...
}
```

## 2. Declaring Java Classes in Rust

To interact with a Java class, you must declare a corresponding Rust struct within the bridge module.

*   **`#[package(com.example.package)]`**: Maps the Rust struct to a specific Java package.
*   **`Signature` Trait**: Every bridged struct must implement `robusta_jni::convert::Signature` to define its JNI signature type (e.g., `"Lcom/example/MyClass;"`).
    *   This can be derived automatically: `#[derive(Signature)]`.
*   **Default Package**: Use `#[package()]` (empty parentheses) for the default/root package.

```rust
use robusta_jni::convert::Signature;

#[bridge]
mod jni {
    #[derive(Signature)]
    #[package(com.myorg.app)]
    struct MyJavaClass;
}
```

## 3. Native Methods (Rust called from Java)

To expose a Rust function to Java (`native` method in Java):

1.  Implement the method inside an `impl` block for the bridged struct.
2.  Mark the function with `pub extern "jni"`.
3.  The function name must match the Java method name.
4.  **Arguments**:
    *   `self` (optional): For non-static methods.
    *   `_env: &JNIEnv` (optional): The first argument (after `self`) can be the JNI environment.
    *   Other arguments must correspond to the Java method signature and implement conversion traits.

```rust
impl MyJavaClass {
    // Java: public native int add(int a, int b);
    pub extern "jni" fn add(self, _env: &JNIEnv, a: i32, b: i32) -> i32 {
        a + b
    }
}
```

## 4. Java Methods (Java called from Rust)

To call an existing Java method from Rust:

1.  Declare the method signature inside an `impl` block for the bridged struct.
2.  Mark the function with `pub extern "java"`.
3.  **No Body**: The function body should be empty `{}`.
4.  **Return Type**: Must be `JniResult<T>` (alias for `jni::errors::Result<T>`) to handle potential JNI exceptions.
5.  **`#[constructor]`**: Use this attribute for constructors. The method name in Rust can be anything (usually `new`), but it maps to `<init>` in JNI.

```rust
impl MyJavaClass {
    // Java: public String toString();
    pub extern "java" fn toString(&self, env: &JNIEnv) -> JniResult<String> {}

    // Java: public static MyJavaClass getInstance();
    pub extern "java" fn getInstance(env: &JNIEnv) -> JniResult<Self> {}
}
```

## 5. Type Conversions (`convert` module)

Robusta handles automatic conversion between Rust and Java types. This is powered by four key traits:

*   **Input to Rust (`native` methods):** `TryFromJavaValue` (fallible) / `FromJavaValue` (infallible).
*   **Output from Rust (`native` methods):** `TryIntoJavaValue` (fallible) / `IntoJavaValue` (infallible).
*   **Input to Java (calling `java` methods):** `TryIntoJavaValue` / `IntoJavaValue`.
*   **Output from Java (calling `java` methods):** `TryFromJavaValue` / `FromJavaValue`.

### Standard Mappings

| Rust Type | Java Type | Notes |
| :--- | :--- | :--- |
| `i32` | `int` | |
| `bool` | `boolean` | |
| `char` | `char` | |
| `i8` | `byte` | |
| `f32` | `float` | |
| `f64` | `double` | |
| `i64` | `long` | |
| `i16` | `short` | |
| `String` | `java.lang.String` | |
| `Vec<T>` | `java.util.ArrayList<T>` | `T` must be convertible. |
| `JObject<'env>` | *(Any Object)* | Use for generic objects (input). |
| `jobject` | *(Any Object)* | Use for generic objects (output). |

### `#[call_type]` Attribute

This attribute on methods controls conversion safety:

*   **`#[call_type(safe)]`** (Default): Uses `TryFrom...`/`TryInto...` traits. Returns/Expects `jni::errors::Result`. Catches Java exceptions and converts them to Rust errors.
*   **`#[call_type(unchecked)]`**: Uses `From...`/`Into...` traits. Faster, but panics on JNI errors or if a Java exception is thrown. Useful for primitive getters/setters or high-performance paths where safety is guaranteed elsewhere.

## 6. Lifetimes: `'env` and `'borrow`

Robusta uses special lifetime names to manage JNI safety:

*   **`'env`**: Represents the lifetime of the `JNIEnv`. Used for JNI objects that are tied to the current native method call scope.
*   **`'borrow`**: Used when borrowing data from Java (e.g., accessing a Java array without copying).

When defining custom structs that hold JNI objects, you may need to declare these lifetimes:

```rust
#[derive(Signature, TryIntoJavaValue, IntoJavaValue, TryFromJavaValue, FromJavaValue)]
#[package(com.my.app)]
struct MyContext<'env, 'borrow> {
    #[instance]
    raw: JObject<'env>,
    // ...
}
```

## 7. Exception Handling

*   **Rust -> Java**: To throw an exception from a `native` method, simply return `Err(jni::errors::Error::JavaException)`. Robusta will translate this into a pending Java exception on the `JNIEnv`.
*   **Java -> Rust**: When calling a `java` method with `#[call_type(safe)]`, any exception thrown in Java will result in the Rust function returning `Err`.

## 8. Limitations

*   **Boxed Primitives**: `Integer`, `Double`, etc., are not automatically unboxed to `i32`, `f64`. Treat them as Objects (`JObject`) or add custom conversion logic.
*   **Generics**: Partial support. `Vec<T>` works, but complex generic mappings might require manual implementation of conversion traits.
