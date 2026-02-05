[INIT].... → Crawl4AI 0.8.0 
<!-- SOURCE: https://sal.dev/android/intro-rust-android-uniffi/ -->
[](https://sal.dev)
Article Image 
Article Image 
# Running Rust on Android with UniFFI
Blog Logo
#### Salvatore Testa
on 20 Mar 2023
[](https://sal.dev/android/intro-rust-android-uniffi/#topofpage)
7 min read 
You don’t want rust in your android, but you might want Rust in your Android.
# Background
I like Kotlin, and I’m very impressed with the content being [written in Rust](https://github.com/rust-unofficial/awesome-rust). I knew it _should be_ possible to call Rust from my Android app. ~~Because I love fighting with the compiler~~ I wanted to see if I could get it working for fun. (I got it working!) I wrote this blog post so others could try it out, and so I could refer back when I try to do something again in the future.
The star of the show is Mozilla’s [UniFFI](https://github.com/mozilla/uniffi-rs/) library that does a lot of the hard work. A high level view is that it generates Rust and Kotlin[1](https://sal.dev/android/intro-rust-android-uniffi/#fn:and_other_languages) that are made for each other. That way your Kotlin code can invoke the Rust methods without worrying about [Foreign Function Interface (FFI)](https://en.wikipedia.org/wiki/Foreign_function_interface) for talking cross-language.
![Glossing over a lot of detail here.](https://sal.dev/assets/article_images/2023-03-20-intro-rust-android-uniffi/Kotlin-UniFFI-Rust.svg)Glossing over a lot of detail here.
The rest of this post will walk through
  * configuring your development environment
  * creating a basic Rust library with UniFFI-generated scaffolding
  * generating Kotlin using UniFFI
  * integrating the Rust and Kotlin in an Android app


I’ll assume you have a basic Rust (via [cargo](https://doc.rust-lang.org/cargo/getting-started/installation.html)) and Android (via [Android Studio](https://developer.android.com/studio)) environment installed.
# Step 1 - Configure your Rust + NDK environment
This was (I believe) the most annoying part to get right. You can either manually configure the Android Native Development Kit (NDK) or you can use `cross` that downloads a Docker image that’s ready to go. I’d recommend setting up the NDK locally (builds faster[2](https://sal.dev/android/intro-rust-android-uniffi/#fn:how_much_faster)), but falling back on `cross` (easier default setup) if you get stuck.
### Option A - Use Docker-based `cross`
  1. Install [Docker Desktop](https://www.docker.com/products/docker-desktop/), [OrbStack](https://orbstack.dev/), [Rancher Desktop](https://docs.rancherdesktop.io/getting-started/installation), or your favorite tool. If you can run `docker run --rm hello-world`, then you’re good.
  2. Install [cross](https://github.com/cross-rs/cross).
  3. If you’re happy with the `minSdkVersion` on `cross` ([seen here](https://github.com/cross-rs/cross/blob/main/docker/Dockerfile.aarch64-linux-android#L17)), you’re done. Otherwise, you’ll need to build new Docker images with the desired Android version ([instructions here](https://github.com/cross-rs/cross/wiki/FAQ#android-version-configuration))
  4. That’s it! Go to “Step 2 - Make a Rust library”.


### Option B - Configure Android NDK locally
Open Android Studio, and navigate to **SDK Manager > SDK Tools > NDK (Side by Side)** as laid out on the [Android Developer site](https://developer.android.com/studio/projects/install-ndk#default-version).
![You can also click "Show package details" to get a specific version.](https://sal.dev/assets/article_images/2023-03-20-intro-rust-android-uniffi/Android-NDK.jpg)You can also click "Show package details" to get a specific version.
Locate which NDK version you have…
```
❯ ls $ANDROID_HOME/ndk
23.1.7779620    25.2.9519653
```

… and set it to your `NDK_PATH` environment variable.
```
❯ NDK_PATH=$ANDROID_HOME/ndk/25.2.9519653
```

<⚠️> Android replaced `libgcc` with `libuwind` in NDK 23 which breaks the compilation step. Fortunately there’s a workaround[3](https://sal.dev/android/intro-rust-android-uniffi/#fn:fixing_ndk23_issue) that I’ll summarize. If you’re using NDK 23.x or higher, you’ll either need to use a `nightly` version of Rust _or_ run the following from your terminal.
```
# if your NDK version is ≥ 23 run this
# snippet that fixes the "broken" NDK issue
❯ find $NDK_PATH -name 'libunwind.a' | \
  sed 's@libunwind.a$@libgcc.a@' | \
  while read x; do
    echo "INPUT(-lunwind)" > $x
  done
```

</⚠️>
You’ll be able to see the C libraries for each of the architecture-Android version combinations. I’ve modified the output to be more readable.
```
❯ find $NDK_PATH/toolchains/llvm -name "*-linux-android*-clang" | sort -r
$NDK_PATH/path/to/aarch64-linux-android33-clang
$NDK_PATH/path/to/aarch64-linux-android32-clang
$NDK_PATH/path/to/aarch64-linux-android31-clang
# ...
```

I’m going to build for an Android `minSdkVersion` of 24, so these are the four libraries I’ll use.
```
❯ find $NDK_PATH/toolchains/llvm -name "*-linux-android*24-clang" | sort -r
$NDK_PATH/path/to/x86_64-linux-android24-clang
$NDK_PATH/path/to/i686-linux-android24-clang
$NDK_PATH/path/to/armv7a-linux-androideabi24-clang
$NDK_PATH/path/to/aarch64-linux-android24-clang
```

Open (or create) your `$HOME/.cargo/config` file. Add each of the target linkers. Please note:
  * The path has to be absolute.
  * `armv7a`’s target name and clang name are different and it is “androideabi” as opposed to “android”.

```
# ~/.cargo/config
# ...
[target.x86_64-linux-android]
linker = "/Users/sal/Library/Android/sdk/ndk/25.2.9519653/toolchains/llvm/prebuilt/darwin-x86_64/bin/x86_64-linux-android24-clang"

[target.i686-linux-android]
linker = "/Users/sal/Library/Android/sdk/ndk/25.2.9519653/toolchains/llvm/prebuilt/darwin-x86_64/bin/i686-linux-android24-clang"

[target.armv7-linux-androideabi]
linker = "/Users/sal/Library/Android/sdk/ndk/25.2.9519653/toolchains/llvm/prebuilt/darwin-x86_64/bin/armv7a-linux-androideabi24-clang"

[target.aarch64-linux-android]
linker = "/Users/sal/Library/Android/sdk/ndk/25.2.9519653/toolchains/llvm/prebuilt/darwin-x86_64/bin/aarch64-linux-android24-clang"
```

Finally, add the targets to your Rust environment.
```
❯ rustup target add \
    x86_64-linux-android \
    i686-linux-android \
    armv7-linux-androideabi \
    aarch64-linux-android
```

# Step 2 - Make a Rust library
For our example, we’re going to make a simple library that has two methods: reverse a string (“hello” -> “olleh”) and reverse an integer (123 -> 321).
Let’s start by making the library using `cargo`.
```
cargo new reverse-rs --lib
```

Inside the generated `src/lib.rs` file, I throw in some (ChatGPT-assisted) Rust code to reverse a string and integer as well as some tests.
```
# reverse-rs/src/lib.rs
pub fn reverse_string(input_string: &str) -> String {
    input_string.chars().rev().collect()
}

pub fn reverse_integer(input_integer: i32) -> i32 {
    let reversed = input_integer.to_string().chars().rev().collect::<String>();
    reversed.parse::<i32>().unwrap()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_reverses_strings() {
        let result = reverse_string("hello world");
        assert_eq!(result, "dlrow olleh");
    }

    #[test]
    fn it_reverses_integers() {
        let result = reverse_integer(123);
        assert_eq!(result, 321);
    }
}
```

From the `reverse-rs/` folder, run `cargo test` and make sure everything looks good.
# Step 3 - Prepare the Rust for Android
Here’s where the UniFFI magic comes in! We’re going to define our reverse string and integer methods in UniFFI’s special language which we’ll then use to generate both the Rust and Kotlin code.
### Update dependencies
Update the `Cargo.toml` file to look like this.
```
# reverse-rs/Cargo.toml
[package]
name = "reverse-rs"
version = "0.1.0"
edition = "2021"

[lib]
name = "reverse"
crate-type = ["cdylib"]

[dependencies]
uniffi = { version = "0.23.0" }

[build-dependencies]
uniffi = { version = "0.23.0", features = [ "build" ] }
```

This snippet does three key things.
  1. Make the library a [cdylib](https://doc.rust-lang.org/reference/linkage.html) crate. I dropped the `-rs` from the name because hyphens aren’t allowed.
  2. Add `uniffi` as a dependency.
  3. Add `uniffi` as a build dependency.


### Write the UDL file
UniFFI uses it’s own special [UniFFI Definition Language (UDL)](https://mozilla.github.io/uniffi-rs/udl_file_spec.html) for describing interfaces. I made `src/reverse.udl`.
```
// reverse-rs/src/reverse.udl
namespace reverse {
  string reverse_string([ByRef] string input_string);
  i32 reverse_integer(i32 input_integer);
};
```

### Write the Rust generator
Create a build file in the the top level folder (i.e. `reverse-rs/build.rs`) and have it point to the UDL file.
```
# reverse-rs/build.rs
fn main() {
    uniffi::generate_scaffolding("./src/reverse.udl").unwrap();
}
```

Add the `uniffi::include_scaffolding` macro on the top of the `lib.rs` file, to generate the Rust scaffolding.
```
# reverse-rs/src/lib.rs
uniffi::include_scaffolding!("reverse");

pub fn reverse_string(input_string: &str) -> String {
// ...
```

# Step 4 - Compile the Rust library
If on step 1 you setup `cross` use that, or if you went through all the NDK-related steps, use `cargo build ...`.
```
# reverse-rs/
# if you're using cross (step 1, option A)
❯ cross build --target x86_64-linux-android && \
    cross build --target i686-linux-android && \
    cross build --target armv7-linux-androideabi && \
    cross build --target aarch64-linux-android

# if you have the NDK setup (step 1, option B)
❯ cargo build --lib \
    --target x86_64-linux-android \
    --target i686-linux-android \
    --target armv7-linux-androideabi \
    --target aarch64-linux-android
```

The end result will be a `.so` file in your corresponding `target/` folder!
```
# reverse-rs/
❯ for binary in target/*/*/libreverse.so; do file $binary; done
target/aarch64-linux-android/debug/libreverse.so: ELF 64-bit LSB shared object, ARM aarch64, version 1 (SYSV), dynamically linked, with debug_info, not stripped
target/armv7-linux-androideabi/debug/libreverse.so: ELF 32-bit LSB shared object, ARM, EABI5 version 1 (SYSV), dynamically linked, with debug_info, not stripped
target/i686-linux-android/debug/libreverse.so: ELF 32-bit LSB shared object, Intel 80386, version 1 (SYSV), dynamically linked, with debug_info, not stripped
target/x86_64-linux-android/debug/libreverse.so: ELF 64-bit LSB shared object, x86-64, version 1 (SYSV), dynamically linked, with debug_info, not stripped
```

To get these ready for the Android app you’ll need to:
  1. move everything to the appropriate [Android ABI](https://developer.android.com/ndk/guides/abis#sa) directory in a `jniLibs/` folder
  2. rename `libreverse.so` to `libuniffi_reverse.so`


Here’s a command that will do all of it for you.
```
# reverse-rs/
❯ mkdir -p jniLibs/arm64-v8a/ && \
  cp target/aarch64-linux-android/debug/libreverse.so jniLibs/arm64-v8a/libuniffi_reverse.so && \
  mkdir -p jniLibs/armeabi-v7a/ && \
    cp target/armv7-linux-androideabi/debug/libreverse.so jniLibs/armeabi-v7a/libuniffi_reverse.so && \
  mkdir -p jniLibs/x86/ && \
    cp target/i686-linux-android/debug/libreverse.so jniLibs/x86/libuniffi_reverse.so && \
  mkdir -p jniLibs/x86_64/ && \
    cp target/x86_64-linux-android/debug/libreverse.so jniLibs/x86_64/libuniffi_reverse.so
```

Here’s where you’ll be at the end.
```
# reverse-rs/
❯ tree jniLibs
jniLibs
├── arm64-v8a
│   └── libuniffi_reverse.so
├── armeabi-v7a
│   └── libuniffi_reverse.so
├── x86
│   └── libuniffi_reverse.so
└── x86_64
    └── libuniffi_reverse.so

5 directories, 4 files
```

# Step 5 - Generate the Kotlin methods
Add the following to the bottom of your `Cargo.toml` file.
```
# reverse-rs/Cargo.toml
# ...

[[bin]]
name = "uniffi-bindgen"
path = "uniffi-bindgen.rs"
```

Make the `reverse-rs/uniffi-bindgen.rs` file.
```
# reverse-rs/uniffi-bindgen.rs
fn main() {
    uniffi::uniffi_bindgen_main()
}
```

Then generate the Kotlin code!
```
# reverse-rs/
❯ cargo run --features=uniffi/cli \
    --bin uniffi-bindgen \
    generate src/reverse.udl \
    --language kotlin
```

This creates a new file `reverse-rs/src/uniffi/reverse/reverse.kt` with a ton of boilerplate but also our methods!
```
// reverse-rs/src/uniffi/reverse/reverse.kt
// ...
fun `reverseString`(`inputString`: String): String {
    return FfiConverterString.lift(
    rustCall() { _status ->
    _UniFFILib.INSTANCE.reverse_b8c9_reverse_string(FfiConverterString.lower(`inputString`), _status)
})
}

fun `reverseInteger`(`inputInteger`: Int): Int {
    return FfiConverterInt.lift(
    rustCall() { _status ->
    _UniFFILib.INSTANCE.reverse_b8c9_reverse_integer(FfiConverterInt.lower(`inputInteger`), _status)
})
}
```

# Step 6 - Create the Android app
For demonstration purposes, I’m going to make a new project via **Android Studio > File > New Project…** and use the “Empty Activity” template, but I’m assuming you’re familiar with Android development and can make your own choices.
### Add the JNA dependency
The UniFFI library depends on Java Native Access (JNA), so add the `@aar` dependency.
```
// reverse-android/app/build.gradle
// ...
dependencies {
  // ...
  implementation "net.java.dev.jna:jna:5.13.0@aar"
  // ...
}
```

Make sure to sync your Gradle files.
### Copy over generated files
  1. Move the `reverse-rs/jniLibs/` folder into `app/src/main/`.
  2. Move the `reverse-rs/src/uniffi/` folder into `app/src/main/java/`.


![You should end up here.](https://sal.dev/assets/article_images/2023-03-20-intro-rust-android-uniffi/Project-Layout.jpg)You should end up here.
### Use the generate Kotlin library
Your IDE will now autocomplete, and you’ll have access to `uniffi.reverse.reverseString` and `uniffi.reverse.reverseInteger`. Here’s what my class looks like.
```
class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    // ...
    val helloWorld = uniffi.reverse.reverseString("Hello World!")
    val oneTwoThree = uniffi.reverse.reverseInteger(123)
    textView.text = "'Hello World!' & '123' becomes '$helloWorld' & '$oneTwoThree'"
  }
}
```

Run it and 🤞🏼 that you don’t have any errors!
![We did it!](https://sal.dev/assets/article_images/2023-03-20-intro-rust-android-uniffi/Android-Hello-World.png)We did it!
Congratulations! You’re running Rust in Android!
# Bonus - Suggestions and Resources
There are a few tweaks that you can do and other things I came across that you might find interesting/helpful.
### Optimize with `--release`
When you `cross build` or `cargo build`, adding the `--release` flag really cuts down on size (but it ~doubles the build time).
```
❯ ls -lh target/*/*/libreverse.so
 37M target/aarch64-linux-android/debug/libreverse.so
4.2M target/aarch64-linux-android/release/libreverse.so

 35M target/armv7-linux-androideabi/debug/libreverse.so
3.5M target/armv7-linux-androideabi/release/libreverse.so

 34M target/i686-linux-android/debug/libreverse.so
3.5M target/i686-linux-android/release/libreverse.so

 37M target/x86_64-linux-android/debug/libreverse.so
4.0M target/x86_64-linux-android/release/libreverse.so
```

### Move `uniffi-bindgen` to its own crate
If you want to iterate faster on your Rust + Kotlin, you’ll need to have the `uniffi-bindgen` logic in [it’s own crate](https://mozilla.github.io/uniffi-rs/tutorial/foreign_language_bindings.html#multi-crate-workspaces). Otherwise, you’ll hit [this error](https://github.com/mozilla/uniffi-rs/issues/1482).
### Helpful Docker guide
[Guillaume Endignoux](https://gendignoux.com/)’s very thorough blog post, [Compiling Rust libraries for Android apps: a deep dive](https://gendignoux.com/blog/2022/10/24/rust-library-android.html), was super helpful for me. It is _much_ more comprehensive that my post.
### More than just UniFFI
There is a neat alternative to UniFFI called [Diplomat](https://github.com/rust-diplomat/diplomat/) for which [Mark Hammond](https://github.com/mhammond)(from Mozilla) wrote a nice comparison, [Comparing UniFFI with Diplomat](https://github.com/mozilla/uniffi-rs/blob/main/docs/diplomat-and-macros.md).
I’m personally excited for [uniffi-kotlin-multiplatform-bindings](https://gitlab.com/trixnity/uniffi-kotlin-multiplatform-bindings) which is still new-ish but could really move the Kotlin ecosystem forward.
### 2023-07-05 Update
[Lammert Westerhoff](https://github.com/lammertw) helpfully pointed out that if you run `cargo build` with the `--lib` flag (in step 4), the subsequent `bin` additions to the `Cargo.toml` (in step 5) won’t break future attempts at `cargo build`ing. I’ve updated the code block in step 4 to include the `--lib` flag.
[heinrich5991](https://github.com/heinrich5991) also [mentioned something similar earlier](https://github.com/mozilla/uniffi-rs/issues/1482#issuecomment-1550888476), but I did not apply their feedback to my blog post. 🤦
### Thank you to my friends
Special thanks to my friends who helped me with this post.
  * [Richard Moot](https://moot.dev/) - workshopping the title and hook
  * [Gary Guo](https://idunnololz.com/) - correcting my poor grammar and helping with the flow
  * [Ray Ryan](https://mastodon.social/@rjrjr) - trying the recipe out, finding _quite a few_ issues, and letting me know about them before I embarrassed myself


### Let me know what you think!
Please feel free to reach out on email, the Fediverse [@sal@fedi.sal.dev](https://fedi.sal.dev/users/sal), or Twitter [@SalTesta14](https://twitter.com/SalTesta14).
* * *
  1. … and [other languages](https://mozilla.github.io/uniffi-rs/#supported-languages)! [↩](https://sal.dev/android/intro-rust-android-uniffi/#fnref:and_other_languages)
  2. On my 2016 MacBook Pro, the `cargo build` took ~1.5 minutes while the `cross build` took ~6 minutes. [↩](https://sal.dev/android/intro-rust-android-uniffi/#fnref:how_much_faster)
  3. Thank you to [Tilmann Meyer](https://github.com/ATiltedTree) in [this GitHub thread](https://github.com/rust-lang/rust/pull/85806#issue-906448858) for laying out the problem! Thank you to [ssrlive](https://github.com/ssrlive) and [Caleb James DeLisle](https://github.com/cjdelisle) for [the fixes](https://github.com/rust-lang/rust/pull/85806#issuecomment-1096266946). [↩](https://sal.dev/android/intro-rust-android-uniffi/#fnref:fixing_ndk23_issue)


[ twitter ](https://twitter.com/share?text=Running+Rust+on+Android+with+UniFFI&url=https://sal.dev/android/intro-rust-android-uniffi)
##### Written by
Blog Logo
#### Salvatore Testa
* * *
Published 20 Mar 2023
##### Supported by
Proudly published with [ Jekyll](http://jekyllrb.com) [ ](https://sal.dev/feed.xml)
All content copyright [Salvatore Testa](https://sal.dev/android/intro-rust-android-uniffi/) © 2025  
All rights reserved.
Image 
# Salvatore's Blog
## It works on my computer. 
[Back to Overview](https://sal.dev/)
­


---


