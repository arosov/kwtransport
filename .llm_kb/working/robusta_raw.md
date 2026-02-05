Directory structure:
└── giovanniberti-robusta/
    ├── README.md
    ├── Cargo.toml
    ├── LICENSE
    ├── .rustfmt.toml
    ├── robusta-android-example/
    │   ├── Cargo.toml
    │   ├── robustaAndroidExample/
    │   │   ├── gradle.properties
    │   │   ├── gradlew
    │   │   ├── gradlew.bat
    │   │   ├── app/
    │   │   │   ├── proguard-rules.pro
    │   │   │   └── src/
    │   │   │       ├── androidTest/
    │   │   │       │   └── java/
    │   │   │       │       └── com/
    │   │   │       │           └── example/
    │   │   │       │               └── robustaandroidexample/
    │   │   │       │                   └── ExampleInstrumentedTest.kt
    │   │   │       ├── main/
    │   │   │       │   ├── AndroidManifest.xml
    │   │   │       │   ├── java/
    │   │   │       │   │   └── com/
    │   │   │       │   │       └── example/
    │   │   │       │   │           └── robustaandroidexample/
    │   │   │       │   │               ├── MainActivity.kt
    │   │   │       │   │               └── RobustaAndroidExample.java
    │   │   │       │   └── res/
    │   │   │       │       ├── drawable/
    │   │   │       │       │   └── ic_launcher_background.xml
    │   │   │       │       ├── drawable-v24/
    │   │   │       │       │   └── ic_launcher_foreground.xml
    │   │   │       │       ├── layout/
    │   │   │       │       │   └── activity_main.xml
    │   │   │       │       ├── mipmap-anydpi-v26/
    │   │   │       │       │   ├── ic_launcher.xml
    │   │   │       │       │   └── ic_launcher_round.xml
    │   │   │       │       ├── mipmap-hdpi/
    │   │   │       │       │   ├── ic_launcher.webp
    │   │   │       │       │   └── ic_launcher_round.webp
    │   │   │       │       ├── mipmap-mdpi/
    │   │   │       │       │   ├── ic_launcher.webp
    │   │   │       │       │   └── ic_launcher_round.webp
    │   │   │       │       ├── mipmap-xhdpi/
    │   │   │       │       │   ├── ic_launcher.webp
    │   │   │       │       │   └── ic_launcher_round.webp
    │   │   │       │       ├── mipmap-xxhdpi/
    │   │   │       │       │   ├── ic_launcher.webp
    │   │   │       │       │   └── ic_launcher_round.webp
    │   │   │       │       ├── mipmap-xxxhdpi/
    │   │   │       │       │   ├── ic_launcher.webp
    │   │   │       │       │   └── ic_launcher_round.webp
    │   │   │       │       ├── values/
    │   │   │       │       │   ├── colors.xml
    │   │   │       │       │   ├── strings.xml
    │   │   │       │       │   └── themes.xml
    │   │   │       │       └── values-night/
    │   │   │       │           └── themes.xml
    │   │   │       └── test/
    │   │   │           └── java/
    │   │   │               └── com/
    │   │   │                   └── example/
    │   │   │                       └── robustaandroidexample/
    │   │   │                           └── ExampleUnitTest.kt
    │   │   └── gradle/
    │   │       └── wrapper/
    │   │           └── gradle-wrapper.properties
    │   └── src/
    │       ├── lib.rs
    │       └── thread_func.rs
    ├── robusta-codegen/
    │   ├── Cargo.toml
    │   └── src/
    │       ├── lib.rs
    │       ├── utils.rs
    │       ├── validation.rs
    │       ├── derive/
    │       │   ├── convert.rs
    │       │   ├── mod.rs
    │       │   ├── signature.rs
    │       │   └── utils.rs
    │       └── transformation/
    │           ├── context.rs
    │           ├── exported.rs
    │           ├── imported.rs
    │           ├── mod.rs
    │           └── utils.rs
    ├── robusta-example/
    │   ├── Cargo.toml
    │   ├── Makefile
    │   ├── com/
    │   │   └── example/
    │   │       └── robusta/
    │   │           └── HelloWorld.java
    │   └── src/
    │       └── lib.rs
    ├── src/
    │   ├── lib.rs
    │   └── convert/
    │       ├── field.rs
    │       ├── mod.rs
    │       ├── safe.rs
    │       └── unchecked.rs
    ├── tests/
    │   ├── mod.rs
    │   └── driver/
    │       ├── gradlew
    │       ├── gradlew.bat
    │       ├── gradle/
    │       │   └── wrapper/
    │       │       └── gradle-wrapper.properties
    │       ├── native/
    │       │   ├── Cargo.toml
    │       │   └── src/
    │       │       └── lib.rs
    │       └── src/
    │           ├── main/
    │           │   └── java/
    │           │       └── User.java
    │           └── test/
    │               └── java/
    │                   └── UserTest.java
    └── .github/
        ├── dependabot.yml
        └── workflows/
            ├── dependabot-auto-merge.yml
            ├── release.yml
            ├── setup_and_test.yml
            └── test.yml

================================================
FILE: README.md
================================================
# robusta &mdash; easy interop between Rust and Java
[![Build Status](https://github.com/giovanniberti/robusta/actions/workflows/test.yml/badge.svg)](https://github.com/giovanniberti/robusta/actions/workflows/test.yml) [![Latest Version](https://img.shields.io/crates/v/robusta_jni.svg)](https://crates.io/crates/robusta_jni) [![Docs](https://docs.rs/robusta_jni/badge.svg?version=0.2.0)](https://docs.rs/robusta_jni)

[Master branch docs](https://giovanniberti.github.io/doc/robusta_jni/)

This library provides a procedural macro to make easier to write JNI-compatible code in Rust.

It can perform automatic conversion of Rust-y input and output types (see the [limitations](#limitations)).

```toml
[dependencies]
robusta_jni = "0.2"
```

## Usage
All that's needed is a couple of attributes in the right places.

First, a `#[bridge]` attribute on a module will enable it to be processed by `robusta`.

Then, we will need a struct for every class with a native method that will be implemented in Rust,
and each of these structs will have to be annotated with a `#[package]` attribute
with the name of the Java package the corresponding class belongs to.

After that, the functions implemented can be written as ordinary Rust functions, and the macro will
take care of converting to and from Java types for functions marked public and with a `"jni"` ABI. By default if a conversion fails a Java exception is thrown.

On the other hand, if you need to call Java function from Rust, you add a `"java"` ABI and add a  `&JNIEnv` parameter after `self`/`&self`/`&mut self` (or as first parameter if the method is static), and leave the function body empty.

On these methods you can attach a `call_type` attribute that manages how conversions and errors are handled: by default, `#[call_type(safe)]` is implied,
but you can switch to `#[call_type(unchecked)]` at any time, most likely with few or no code changes.

You can also force a Java type on input arguments via `#[input_type]` attribute, which can be useful for Android JNI development for example.

### Android specificities

On Android App, to call a Java class from rust the JVM use the callstack to find desired class.
But when in a rust thread, you don't have a call stack anymore.\
So to be able to call a Java class you have to pass the class reference rather than the string class path.

You can find an example of this usage in `robusta-android-example/src/thread_func.rs`

## Code example

You can find an example under `./robusta-example`. To run it you should have `java` and `javac` on your PATH and then execute:

```bash
$ cd robusta-example
$ make java_run

# if you don't have `make` installed:
$ cargo build && javac com/example/robusta/HelloWorld.java && RUST_BACKTRACE=full java -Djava.library.path=../target/debug com.example.robusta.HelloWorld
```

### Usage on Android example

You can find an example of Robusta used for Android in `./robusta-android-example`.
To run it, open the project robustaAndroidExample with Android Studio.

Cargo build is automatically run by gradle.

The rust lib.rs is the image of the Java class RobustaAndroidExample.

This example only gets the files authorized path of the App.

## Example usage
### Rust side
```rust
use robusta_jni::bridge;
use robusta_jni::convert::Signature;

#[bridge]
mod jni {
    #[derive(Signature)]
    #[package(com.example.robusta)]
    struct HelloWorld;

    impl HelloWorld {
        pub extern "jni" fn special(mut input1: Vec<i32>, input2: i32) -> Vec<String> {
            input1.push(input2);
            input1.iter().map(ToString::to_string).collect()
        }
    }
}
```

### Java side
```java
package com.example.robusta;

import java.util.*;

class HelloWorld {
    private static native ArrayList<String> special(ArrayList<Integer> input1, int input2);

    static {
        System.loadLibrary("robusta_example");
    }

    public static void main(String[] args) {
        ArrayList<String> output = HelloWorld.special(new ArrayList<Integer>(List.of(1, 2, 3)), 4);
        System.out.println(output)
    }
}
```

## Type conversion details and extension to custom types
There are four traits that control how Rust types are converted to/from Java types:
`(Try)FromJavaValue` and `(Try)IntoJavaValue`.

These traits are used for input and output types respectively, and implementing them
is necessary to allow the library to perform automatic type conversion.

These traits make use of type provided by the  [`jni`](https://crates.io/crates/jni) crate,
however to provide maximum compatibility with `robusta`, we suggest using the re-exported version under `robusta_jni::jni`.

### Raising exceptions
You can make a Rust native method raise a Java exception simply by returning a `jni::errors::Result` with an `Err` variant.

### Conversion table

| **Rust**                                                                           | **Java**                          |
|------------------------------------------------------------------------------------|-----------------------------------|
| i32                                                                                | int                               |
| bool                                                                               | boolean                           |
| char                                                                               | char                              |
| i8                                                                                 | byte                              |
| f32                                                                                | float                             |
| f64                                                                                | double                            |
| i64                                                                                | long                              |
| i16                                                                                | short                             |
| String                                                                             | String                            |
| Vec\<T\>†                                                                          | ArrayList\<T\>                    |
| Box<[u8]>                                                                          | byte[]                            |
| [jni::JObject<'env>](https://docs.rs/jni/0.17.0/jni/objects/struct.JObject.html) ‡ | *(any Java object as input type)* |
| [jni::jobject](https://docs.rs/jni/0.17.0/jni/sys/type.jobject.html)               | *(any Java object as output)*     |

† Type parameter `T` must implement proper conversion types

‡ The special `'env` lifetime **must** be used

## Limitations

Currently there are some limitations in the conversion mechanism:
 * Boxed types are supported only through the opaque `JObject`/`jobject` types
 * Automatic type conversion is limited to the table outlined above, though easily extendable if needed.


## Contributing
I glady accept external contributions! :)



================================================
FILE: Cargo.toml
================================================
[package]
name = "robusta_jni"
version = "0.2.2"
authors = ["Giovanni Berti <dev.giovanniberti@gmail.com>"]
repository = "https://github.com/giovanniberti/robusta"
description = "Easy interop between Rust and Java"
keywords = ["ffi", "jni", "java"]
license = "MIT"
edition = "2018"
readme = "README.md"
categories = ["development-tools::ffi", "api-bindings"]
exclude = ["/robusta-codegen", "/robusta-example", "README.md", "/robusta-android-example"]
documentation = "https://docs.rs/robusta/"

[dependencies]
robusta-codegen = { version = "0.2", path = "./robusta-codegen" }
jni = "^0.20"
paste = "^1"
static_assertions = "^1"

[dev-dependencies]
native = { path = "./tests/driver/native" }
jni = { version = "^0.20", features = ["invocation"] }

[workspace]
members = ["robusta-codegen", "robusta-example", "tests/driver/native", "robusta-android-example"]



================================================
FILE: LICENSE
================================================
Copyright © 2020 Giovanni Berti

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


================================================
FILE: .rustfmt.toml
================================================
brace_style = "PreferSameLine"



================================================
FILE: robusta-android-example/Cargo.toml
================================================
[package]
name = "robusta-android-example"
version = "0.2.2"
authors = ["Elise Chouleur"]
edition = "2018"

[lib]
name = "robustaandroidexample"
crate-type = ["cdylib"]

[dependencies]
robusta_jni = { path = "../.", version = "0.2" }
jni = "^0.20"
android_logger = "^0"
log = "^0"



================================================
FILE: robusta-android-example/robustaAndroidExample/gradle.properties
================================================
# Project-wide Gradle settings.
# IDE (e.g. Android Studio) users:
# Gradle settings configured through the IDE *will override*
# any settings specified in this file.
# For more details on how to configure your build environment visit
# http://www.gradle.org/docs/current/userguide/build_environment.html
# Specifies the JVM arguments used for the daemon process.
# The setting is particularly useful for tweaking memory settings.
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
# When configured, Gradle will run in incubating parallel mode.
# This option should only be used with decoupled projects. More details, visit
# http://www.gradle.org/docs/current/userguide/multi_project_builds.html#sec:decoupled_projects
# org.gradle.parallel=true
# AndroidX package structure to make it clearer which packages are bundled with the
# Android operating system, and which are packaged with your app"s APK
# https://developer.android.com/topic/libraries/support-library/androidx-rn
android.useAndroidX=true
# Automatically convert third-party libraries to use AndroidX
android.enableJetifier=true
# Kotlin code style for this project: "official" or "obsolete":
kotlin.code.style=official


================================================
FILE: robusta-android-example/robustaAndroidExample/gradlew
================================================
#!/usr/bin/env sh

#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`/" >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo
    echo "$*"
    echo
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
nonstop=false
case "`uname`" in
  CYGWIN* )
    cygwin=true
    ;;
  Darwin* )
    darwin=true
    ;;
  MINGW* )
    msys=true
    ;;
  NONSTOP* )
    nonstop=true
    ;;
esac

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar


# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

# Increase the maximum file descriptors if we can.
if [ "$cygwin" = "false" -a "$darwin" = "false" -a "$nonstop" = "false" ] ; then
    MAX_FD_LIMIT=`ulimit -H -n`
    if [ $? -eq 0 ] ; then
        if [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "max" ] ; then
            MAX_FD="$MAX_FD_LIMIT"
        fi
        ulimit -n $MAX_FD
        if [ $? -ne 0 ] ; then
            warn "Could not set maximum file descriptor limit: $MAX_FD"
        fi
    else
        warn "Could not query maximum file descriptor limit: $MAX_FD_LIMIT"
    fi
fi

# For Darwin, add options to specify how the application appears in the dock
if $darwin; then
    GRADLE_OPTS="$GRADLE_OPTS \"-Xdock:name=$APP_NAME\" \"-Xdock:icon=$APP_HOME/media/gradle.icns\""
fi

# For Cygwin or MSYS, switch paths to Windows format before running java
if [ "$cygwin" = "true" -o "$msys" = "true" ] ; then
    APP_HOME=`cygpath --path --mixed "$APP_HOME"`
    CLASSPATH=`cygpath --path --mixed "$CLASSPATH"`

    JAVACMD=`cygpath --unix "$JAVACMD"`

    # We build the pattern for arguments to be converted via cygpath
    ROOTDIRSRAW=`find -L / -maxdepth 1 -mindepth 1 -type d 2>/dev/null`
    SEP=""
    for dir in $ROOTDIRSRAW ; do
        ROOTDIRS="$ROOTDIRS$SEP$dir"
        SEP="|"
    done
    OURCYGPATTERN="(^($ROOTDIRS))"
    # Add a user-defined pattern to the cygpath arguments
    if [ "$GRADLE_CYGPATTERN" != "" ] ; then
        OURCYGPATTERN="$OURCYGPATTERN|($GRADLE_CYGPATTERN)"
    fi
    # Now convert the arguments - kludge to limit ourselves to /bin/sh
    i=0
    for arg in "$@" ; do
        CHECK=`echo "$arg"|egrep -c "$OURCYGPATTERN" -`
        CHECK2=`echo "$arg"|egrep -c "^-"`                                 ### Determine if an option

        if [ $CHECK -ne 0 ] && [ $CHECK2 -eq 0 ] ; then                    ### Added a condition
            eval `echo args$i`=`cygpath --path --ignore --mixed "$arg"`
        else
            eval `echo args$i`="\"$arg\""
        fi
        i=`expr $i + 1`
    done
    case $i in
        0) set -- ;;
        1) set -- "$args0" ;;
        2) set -- "$args0" "$args1" ;;
        3) set -- "$args0" "$args1" "$args2" ;;
        4) set -- "$args0" "$args1" "$args2" "$args3" ;;
        5) set -- "$args0" "$args1" "$args2" "$args3" "$args4" ;;
        6) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" ;;
        7) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" ;;
        8) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" "$args7" ;;
        9) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" "$args7" "$args8" ;;
    esac
fi

# Escape application args
save () {
    for i do printf %s\\n "$i" | sed "s/'/'\\\\''/g;1s/^/'/;\$s/\$/' \\\\/" ; done
    echo " "
}
APP_ARGS=`save "$@"`

# Collect all arguments for the java command, following the shell quoting and substitution rules
eval set -- $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS "\"-Dorg.gradle.appname=$APP_BASE_NAME\"" -classpath "\"$CLASSPATH\"" org.gradle.wrapper.GradleWrapperMain "$APP_ARGS"

exec "$JAVACMD" "$@"



================================================
FILE: robusta-android-example/robustaAndroidExample/gradlew.bat
================================================
@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Gradle startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar


@rem Execute Gradle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable GRADLE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%GRADLE_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega



================================================
FILE: robusta-android-example/robustaAndroidExample/app/proguard-rules.pro
================================================
# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/androidTest/java/com/example/robustaandroidexample/ExampleInstrumentedTest.kt
================================================
package com.example.robustaandroidexample

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.robustaandroidexample", appContext.packageName)
    }
}


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/AndroidManifest.xml
================================================
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.robustaandroidexample">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.RobustaAndroidExample">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/java/com/example/robustaandroidexample/MainActivity.kt
================================================
package com.example.robustaandroidexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RobustaAndroidExample.runRustExample(applicationContext)
    }
}


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/java/com/example/robustaandroidexample/RobustaAndroidExample.java
================================================
package com.example.robustaandroidexample;

import android.content.Context;
import android.util.Log;

public class RobustaAndroidExample {
    static {
        System.loadLibrary("robustaandroidexample");
    }

    public static native void runRustExample(Context context);

    public static String getAppFilesDir(Context context) {
        Log.d("ROBUSTA_ANDROID_EXAMPLE", "getAppFilesDir IN");
        return context.getFilesDir().toString();
    }
    public static int threadTestNoClass(String s) {
        Log.d("ROBUSTA_ANDROID_EXAMPLE", "threadTestNoClass IN: " + s);
        return 10;
    }
    public static int threadTestWithClass(String s) {
        Log.d("ROBUSTA_ANDROID_EXAMPLE", "threadTestWithClass IN: " + s);
        return 10;
    }

}



================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/res/drawable/ic_launcher_background.xml
================================================
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#3DDC84"
        android:pathData="M0,0h108v108h-108z" />
    <path
        android:fillColor="#00000000"
        android:pathData="M9,0L9,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M19,0L19,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M29,0L29,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M39,0L39,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M49,0L49,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M59,0L59,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M69,0L69,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M79,0L79,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M89,0L89,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M99,0L99,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,9L108,9"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,19L108,19"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,29L108,29"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,39L108,39"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,49L108,49"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,59L108,59"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,69L108,69"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,79L108,79"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,89L108,89"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,99L108,99"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M19,29L89,29"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M19,39L89,39"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M19,49L89,49"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M19,59L89,59"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M19,69L89,69"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M19,79L89,79"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M29,19L29,89"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M39,19L39,89"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M49,19L49,89"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M59,19L59,89"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M69,19L69,89"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M79,19L79,89"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
</vector>



================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/res/drawable-v24/ic_launcher_foreground.xml
================================================
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:aapt="http://schemas.android.com/aapt"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path android:pathData="M31,63.928c0,0 6.4,-11 12.1,-13.1c7.2,-2.6 26,-1.4 26,-1.4l38.1,38.1L107,108.928l-32,-1L31,63.928z">
        <aapt:attr name="android:fillColor">
            <gradient
                android:endX="85.84757"
                android:endY="92.4963"
                android:startX="42.9492"
                android:startY="49.59793"
                android:type="linear">
                <item
                    android:color="#44000000"
                    android:offset="0.0" />
                <item
                    android:color="#00000000"
                    android:offset="1.0" />
            </gradient>
        </aapt:attr>
    </path>
    <path
        android:fillColor="#FFFFFF"
        android:fillType="nonZero"
        android:pathData="M65.3,45.828l3.8,-6.6c0.2,-0.4 0.1,-0.9 -0.3,-1.1c-0.4,-0.2 -0.9,-0.1 -1.1,0.3l-3.9,6.7c-6.3,-2.8 -13.4,-2.8 -19.7,0l-3.9,-6.7c-0.2,-0.4 -0.7,-0.5 -1.1,-0.3C38.8,38.328 38.7,38.828 38.9,39.228l3.8,6.6C36.2,49.428 31.7,56.028 31,63.928h46C76.3,56.028 71.8,49.428 65.3,45.828zM43.4,57.328c-0.8,0 -1.5,-0.5 -1.8,-1.2c-0.3,-0.7 -0.1,-1.5 0.4,-2.1c0.5,-0.5 1.4,-0.7 2.1,-0.4c0.7,0.3 1.2,1 1.2,1.8C45.3,56.528 44.5,57.328 43.4,57.328L43.4,57.328zM64.6,57.328c-0.8,0 -1.5,-0.5 -1.8,-1.2s-0.1,-1.5 0.4,-2.1c0.5,-0.5 1.4,-0.7 2.1,-0.4c0.7,0.3 1.2,1 1.2,1.8C66.5,56.528 65.6,57.328 64.6,57.328L64.6,57.328z"
        android:strokeWidth="1"
        android:strokeColor="#00000000" />
</vector>


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/res/layout/activity_main.xml
================================================
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml
================================================
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml
================================================
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/res/mipmap-hdpi/ic_launcher.webp
================================================
[Binary file]


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/res/mipmap-hdpi/ic_launcher_round.webp
================================================
[Binary file]


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/res/mipmap-mdpi/ic_launcher.webp
================================================
[Binary file]


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/res/mipmap-mdpi/ic_launcher_round.webp
================================================
[Binary file]


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/res/mipmap-xhdpi/ic_launcher.webp
================================================
[Binary file]


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/res/mipmap-xhdpi/ic_launcher_round.webp
================================================
[Binary file]


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/res/mipmap-xxhdpi/ic_launcher.webp
================================================
[Binary file]


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/res/mipmap-xxhdpi/ic_launcher_round.webp
================================================
[Binary file]


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp
================================================
[Binary file]


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.webp
================================================
[Binary file]


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/res/values/colors.xml
================================================
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="purple_200">#FFBB86FC</color>
    <color name="purple_500">#FF6200EE</color>
    <color name="purple_700">#FF3700B3</color>
    <color name="teal_200">#FF03DAC5</color>
    <color name="teal_700">#FF018786</color>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
</resources>


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/res/values/strings.xml
================================================
<resources>
    <string name="app_name">robustaAndroidExample</string>
</resources>


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/res/values/themes.xml
================================================
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Theme.RobustaAndroidExample" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
        <!-- Primary brand color. -->
        <item name="colorPrimary">@color/purple_500</item>
        <item name="colorPrimaryVariant">@color/purple_700</item>
        <item name="colorOnPrimary">@color/white</item>
        <!-- Secondary brand color. -->
        <item name="colorSecondary">@color/teal_200</item>
        <item name="colorSecondaryVariant">@color/teal_700</item>
        <item name="colorOnSecondary">@color/black</item>
        <!-- Status bar color. -->
        <item name="android:statusBarColor" tools:targetApi="l">?attr/colorPrimaryVariant</item>
        <!-- Customize your theme here. -->
    </style>
</resources>


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/main/res/values-night/themes.xml
================================================
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Theme.RobustaAndroidExample" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
        <!-- Primary brand color. -->
        <item name="colorPrimary">@color/purple_200</item>
        <item name="colorPrimaryVariant">@color/purple_700</item>
        <item name="colorOnPrimary">@color/black</item>
        <!-- Secondary brand color. -->
        <item name="colorSecondary">@color/teal_200</item>
        <item name="colorSecondaryVariant">@color/teal_200</item>
        <item name="colorOnSecondary">@color/black</item>
        <!-- Status bar color. -->
        <item name="android:statusBarColor" tools:targetApi="l">?attr/colorPrimaryVariant</item>
        <!-- Customize your theme here. -->
    </style>
</resources>


================================================
FILE: robusta-android-example/robustaAndroidExample/app/src/test/java/com/example/robustaandroidexample/ExampleUnitTest.kt
================================================
package com.example.robustaandroidexample

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}


================================================
FILE: robusta-android-example/robustaAndroidExample/gradle/wrapper/gradle-wrapper.properties
================================================
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-7.2-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists



================================================
FILE: robusta-android-example/src/lib.rs
================================================
pub(crate) mod thread_func;

use ::jni::objects::GlobalRef;
use ::jni::JavaVM;
use robusta_jni::bridge;
use std::sync::OnceLock;

static APP_CONTEXT: OnceLock<(JavaVM, GlobalRef)> = OnceLock::new();

#[bridge]
mod jni {
    use crate::APP_CONTEXT;
    use android_logger::Config;
    use jni::objects::{GlobalRef, JObject, JValue};
    use log::info;
    use robusta_jni::convert::{IntoJavaValue, Signature, TryFromJavaValue, TryIntoJavaValue};
    use robusta_jni::jni::errors::Result as JniResult;
    use robusta_jni::jni::objects::AutoLocal;
    use robusta_jni::jni::JNIEnv;
    use std::thread;

    #[derive(Signature, TryIntoJavaValue, IntoJavaValue, TryFromJavaValue)]
    #[package(com.example.robustaandroidexample)]
    pub struct RobustaAndroidExample<'env: 'borrow, 'borrow> {
        #[instance]
        raw: AutoLocal<'env, 'borrow>,
    }

    impl<'env: 'borrow, 'borrow> RobustaAndroidExample<'env, 'borrow> {
        pub extern "jni" fn runRustExample(self, env: &JNIEnv, context: JObject<'env>) {
            android_logger::init_once(Config::default().with_tag("RUST_ROBUSTA_ANDROID_EXAMPLE"));

            info!("TEST START");
            let java_class = env
                .find_class("com/example/robustaandroidexample/RobustaAndroidExample")
                .unwrap();
            let _ = APP_CONTEXT.set((
                env.get_java_vm().unwrap(),
                env.new_global_ref(java_class).unwrap(),
            ));

            let app_files_dir = RobustaAndroidExample::getAppFilesDir(env, context).unwrap();
            info!("App files dir: {}", app_files_dir);

            assert_eq!(
                RobustaAndroidExample::threadTestNoClass(env, "test".to_string()).unwrap(),
                10
            );

            let test_string = env.new_string("SUPER TEST").unwrap();
            let test_string = JValue::from(test_string);
            let met_call = env.call_static_method(
                "com/example/robustaandroidexample/RobustaAndroidExample",
                "threadTestNoClass",
                "(Ljava/lang/String;)I",
                &[test_string],
            );
            assert!(met_call.is_ok());

            let thread_handler = thread::Builder::new()
                .name("test_thread_fail".to_string())
                .spawn(move || crate::thread_func::thread_test_fail());
            let join_res = thread_handler.unwrap().join().unwrap();
            assert!(join_res.is_err());

            let thread_handler = thread::Builder::new()
                .name("test_thread_good".to_string())
                .spawn(move || crate::thread_func::thread_test_good());
            let join_res = thread_handler.unwrap().join().unwrap();
            assert!(join_res.is_ok());

            info!("TEST END");
        }

        pub extern "java" fn getAppFilesDir(
            env: &JNIEnv,
            #[input_type("Landroid/content/Context;")] context: JObject,
        ) -> JniResult<String> {
        }

        pub extern "java" fn threadTestNoClass(env: &JNIEnv, s: String) -> JniResult<i32> {}
        pub extern "java" fn threadTestWithClass(
            env: &JNIEnv,
            class_ref: &GlobalRef,
            s: String,
        ) -> JniResult<i32> {
        }
    }
}



================================================
FILE: robusta-android-example/src/thread_func.rs
================================================
use crate::jni::RobustaAndroidExample;
use jni::objects::JValue;
use log::{debug, error};

pub(crate) fn thread_test_fail() -> Result<(), String> {
    debug!("TEST_THREAD_FAIL: start...");

    let (app_vm, _) = crate::APP_CONTEXT
        .get()
        .ok_or_else(|| "Couldn't get APP_CONTEXT".to_string())?;
    let env = app_vm
        .attach_current_thread_permanently()
        .map_err(|_| "Couldn't attach to current thread".to_string())?;

    debug!("TEST_THREAD_FAIL: via JNI");
    let test_string = env.new_string("SUPER TEST").unwrap();
    let test_string = JValue::from(test_string);
    if let Err(e) = env.call_static_method(
        "com/example/robustaandroidexample/RobustaAndroidExample",
        "threadTestNoClass",
        "(Ljava/lang/String;)I",
        &[test_string],
    ) {
        error!("Couldn't call method via classic JNI: {}", e);
        if env.exception_check().unwrap_or(false) {
            let _ = env.exception_clear();
        }
    }

    debug!("TEST_THREAD_FAIL: via Robusta");

    /* Call methode */
    if let Err(e) = RobustaAndroidExample::threadTestNoClass(&env, "test".to_string()) {
        let msg = format!("Couldn't call method via Robusta: {}", e);
        error!("{}", msg);
        if env.exception_check().unwrap_or(false) {
            let _ = env.exception_clear();
        }
        return Err(msg);
    }
    Ok(())
}

pub(crate) fn thread_test_good() -> Result<(), String> {
    debug!("TEST_THREAD_GOOD: start...");

    let (app_vm, class_ref) = crate::APP_CONTEXT
        .get()
        .ok_or_else(|| "Couldn't get APP_CONTEXT".to_string())?;
    let env = app_vm
        .attach_current_thread_permanently()
        .map_err(|_| "Couldn't attach to current thread".to_string())?;

    debug!("TEST_THREAD_GOOD: via JNI");
    let test_string = env.new_string("SUPER TEST").unwrap();
    let test_string = JValue::from(test_string);
    if let Err(e) = env.call_static_method(
        class_ref,
        "threadTestNoClass",
        "(Ljava/lang/String;)I",
        &[test_string],
    ) {
        error!("Couldn't call method via classic JNI: {}", e);
        if env.exception_check().unwrap_or(false) {
            let ex = env.exception_occurred().unwrap();
            let _ = env.exception_clear();
            let res = env
                .call_method(ex, "toString", "()Ljava/lang/String;", &[])
                .unwrap()
                .l()
                .unwrap();
            let ex_msg: String = env.get_string(res.into()).unwrap().into();
            error!("check_jni_error: {}", ex_msg);
        }
    }

    debug!("TEST_THREAD_GOOD: via Robusta");

    /* Call methode */
    if let Err(e) = RobustaAndroidExample::threadTestWithClass(&env, class_ref, "test".to_string())
    {
        let msg = format!("Couldn't call method via Robusta: {}", e);
        error!("{}", msg);
        if env.exception_check().unwrap_or(false) {
            let _ = env.exception_clear();
        }
        return Err(msg);
    }
    Ok(())
}



================================================
FILE: robusta-codegen/Cargo.toml
================================================
[package]
name = "robusta-codegen"
version = "0.2.2"
authors = ["Giovanni Berti <dev.giovanniberti@gmail.com>"]
description = "Procedural macro crate to support `robusta`"
keywords = ["proc_macro", "procmacro", "robusta"]
edition = "2018"
categories = ["development-tools::ffi", "api-bindings"]
license = "MIT"
repository = "https://github.com/giovanniberti/robusta/robusta-codegen"

[lib]
proc-macro = true

[dependencies]
quote = "^1"
proc-macro2 = { version = "^1", features = ["span-locations"]}
syn = { version = "^2", features = ["visit", "fold", "derive"] }
proc-macro-error = { version = "^1", default-features = false }
rand = "^0"
darling = "^0"
Inflector = "^0"



================================================
FILE: robusta-codegen/src/lib.rs
================================================
use proc_macro::TokenStream;

use proc_macro_error::proc_macro_error;
use syn::{parse_macro_input, DeriveInput};

use validation::JNIBridgeModule;

use crate::derive::convert::{
    from_java_value_macro_derive, into_java_value_macro_derive, tryfrom_java_value_macro_derive,
    tryinto_java_value_macro_derive,
};
use crate::transformation::ModTransformer;
use derive::signature::signature_macro_derive;

mod derive;
mod transformation;
mod utils;
mod validation;

#[proc_macro_error]
#[proc_macro_attribute]
pub fn bridge(_args: TokenStream, raw_input: TokenStream) -> TokenStream {
    let module_data = parse_macro_input!(raw_input as JNIBridgeModule);

    let mut transformer = ModTransformer::new(module_data);
    let tokens = transformer.transform_module();

    tokens.into()
}

#[proc_macro_error]
#[proc_macro_derive(Signature, attributes(package))]
pub fn signature_derive(raw_input: TokenStream) -> TokenStream {
    let input = parse_macro_input!(raw_input as DeriveInput);

    signature_macro_derive(input).into()
}

#[proc_macro_error]
#[proc_macro_derive(IntoJavaValue, attributes(package, instance, field))]
pub fn into_java_value_derive(raw_input: TokenStream) -> TokenStream {
    let input = parse_macro_input!(raw_input as DeriveInput);

    into_java_value_macro_derive(input).into()
}

#[proc_macro_error]
#[proc_macro_derive(TryIntoJavaValue, attributes(package, instance, field))]
pub fn tryinto_java_value_derive(raw_input: TokenStream) -> TokenStream {
    let input = parse_macro_input!(raw_input as DeriveInput);

    tryinto_java_value_macro_derive(input).into()
}

#[proc_macro_error]
#[proc_macro_derive(FromJavaValue, attributes(package, instance, field))]
pub fn from_java_value_derive(raw_input: TokenStream) -> TokenStream {
    let input = parse_macro_input!(raw_input as DeriveInput);

    from_java_value_macro_derive(input).into()
}

#[proc_macro_error]
#[proc_macro_derive(TryFromJavaValue, attributes(package, instance, field))]
pub fn tryfrom_java_value_derive(raw_input: TokenStream) -> TokenStream {
    let input = parse_macro_input!(raw_input as DeriveInput);

    tryfrom_java_value_macro_derive(input).into()
}



================================================
FILE: robusta-codegen/src/utils.rs
================================================
use std::iter;

use proc_macro_error::emit_error;
use syn::{
    parse_quote, FnArg, Pat, PatIdent, PatType, Path, PathArguments, Signature, Type, TypeReference,
};

pub fn canonicalize_path(path: &Path) -> Path {
    let mut result = path.clone();
    result.segments = result
        .segments
        .into_iter()
        .map(|mut seg| {
            seg.arguments = PathArguments::None;
            seg
        })
        .collect();

    result
}

pub fn is_self_method(signature: &Signature) -> bool {
    signature.inputs.iter().any(|i| match i {
        FnArg::Receiver(_) => true,
        FnArg::Typed(t) => match &*t.pat {
            Pat::Ident(PatIdent { ident, .. }) => ident == "self",
            _ => false,
        },
    })
}

pub fn get_env_arg(signature: Signature) -> (Signature, Option<FnArg>) {
    let self_method = is_self_method(&signature);

    // Check whether second argument (first exluding self) is of type &JNIEnv, if so we take it out from the signature
    let possible_env_arg = if !self_method {
        signature.inputs.iter().next()
    } else {
        signature.inputs.iter().nth(1)
    };

    let has_explicit_env_arg = if let Some(FnArg::Typed(PatType { ty, .. })) = possible_env_arg {
        if let Type::Reference(TypeReference { elem, .. }) = &**ty {
            if let Type::Path(t) = &**elem {
                let full_path: Path = parse_quote! { ::robusta_jni::jni::JNIEnv };
                let imported_path: Path = parse_quote! { JNIEnv };
                let canonicalized_type_path = canonicalize_path(&t.path);

                canonicalized_type_path == imported_path || canonicalized_type_path == full_path
            } else {
                false
            }
        } else if let Type::Path(t) = &**ty {
            /* If the user has input `env: JNIEnv` instead of `env: &JNIEnv`, we let her know. */
            let full_path: Path = parse_quote! { ::robusta_jni::jni::JNIEnv };
            let imported_path: Path = parse_quote! { JNIEnv };
            let canonicalized_type_path = canonicalize_path(&t.path);

            if canonicalized_type_path == imported_path || canonicalized_type_path == full_path {
                emit_error!(
                    t,
                    "explicit environment parameter must be of type `&JNIEnv`"
                );
            }

            false
        } else {
            false
        }
    } else {
        false
    };

    let (transformed_signature, env_arg): (Signature, Option<FnArg>) = if has_explicit_env_arg {
        let mut inner_signature = signature;

        let mut iter = inner_signature.inputs.into_iter();

        if self_method {
            let self_arg = iter.next();
            let env_arg = iter.next();

            inner_signature.inputs = iter::once(self_arg.unwrap()).chain(iter).collect();
            (inner_signature, env_arg)
        } else {
            let env_arg = iter.next();
            inner_signature.inputs = iter.collect();

            (inner_signature, env_arg)
        }
    } else {
        (signature, None)
    };

    (transformed_signature, env_arg)
}

pub fn get_class_arg_if_any(signature: Signature) -> (Signature, Option<FnArg>) {
    let has_explicit_class_ref_arg = if let Some(FnArg::Typed(PatType { ty, .. })) = signature.inputs.iter().next() {
        if let Type::Reference(TypeReference { elem, .. }) = &**ty {
            if let Type::Path(t) = &**elem {
                let full_path: Path = parse_quote! { ::robusta_jni::jni::objects::GlobalRef };
                let imported_path: Path = parse_quote! { GlobalRef };
                let canonicalized_type_path = canonicalize_path(&t.path);

                canonicalized_type_path == imported_path || canonicalized_type_path == full_path
            } else {
                false
            }
        } else if let Type::Path(t) = &**ty {
            /* If the user has input `class_ref: GlobalRef` instead of `class_ref: &GlobalRef`, we let them know. */
            let full_path: Path = parse_quote! { ::robusta_jni::jni::objects::GlobalRef };
            let imported_path: Path = parse_quote! { GlobalRef };
            let canonicalized_type_path = canonicalize_path(&t.path);

            if canonicalized_type_path == imported_path || canonicalized_type_path == full_path {
                emit_error!(t, "explicit environment parameter must be of type `&GlobalRef`");
            }

            false
        } else {
            false
        }
    } else {
        false
    };

    if has_explicit_class_ref_arg {
        let mut inner_signature = signature;

        let mut iter = inner_signature.inputs.into_iter();
        let class_arg = iter.next();

        inner_signature.inputs = iter.collect();
        (inner_signature, class_arg)

    } else {
        (signature, None)
    }
}

pub fn get_abi(sig: &Signature) -> Option<String> {
    sig.abi
        .as_ref()
        .and_then(|l| l.name.as_ref().map(|n| n.value()))
}



================================================
FILE: robusta-codegen/src/validation.rs
================================================
use core::option::Option::{None, Some};
use core::result::Result::{Err, Ok};
use std::collections::BTreeMap;

use proc_macro_error::{emit_error, emit_warning};
use quote::ToTokens;
use syn::parse::{Parse, ParseBuffer};
use syn::spanned::Spanned;
use syn::visit::Visit;
use syn::{Attribute, Error, GenericParam, Item, ItemImpl, ItemMod, ItemStruct, Result, Type};

use crate::transformation::JavaPath;

struct AttribItemChecker {
    valid: bool,
}

impl AttribItemChecker {
    fn new() -> Self {
        AttribItemChecker { valid: true }
    }
}

impl<'ast> Visit<'ast> for AttribItemChecker {
    fn visit_item(&mut self, node: &'ast Item) {
        let has_package_attribute =
            |a: &Attribute| a.path().segments.first().unwrap().ident == "package";
        match node {
            Item::Struct(_) => {}
            Item::Const(i) if i.attrs.iter().any(has_package_attribute) => {
                emit_error!(i.span(), "`package` attribute used on non-struct type");
                self.valid = false;
            }
            Item::Enum(i) if i.attrs.iter().any(has_package_attribute) => {
                emit_error!(i.span(), "`package` attribute used on non-struct type"; help = i.enum_token.span() => "replace `enum` with `struct`");
                self.valid = false;
            }
            Item::ExternCrate(i) if i.attrs.iter().any(has_package_attribute) => {
                emit_error!(i.span(), "`package` attribute used on non-struct type");
                self.valid = false;
            }
            Item::Fn(i) if i.attrs.iter().any(has_package_attribute) => {
                emit_error!(i.span(), "`package` attribute used on non-struct type");
                self.valid = false;
            }
            Item::ForeignMod(i) => {
                emit_error!(i.span(), "`package` attribute used on non-struct type");
                self.valid = false;
            }
            Item::Impl(i) if i.attrs.iter().any(has_package_attribute) => {
                emit_error!(i.span(), "`package` attribute used on non-struct type");
                self.valid = false;
            }
            Item::Macro(i) if i.attrs.iter().any(has_package_attribute) => {
                emit_error!(i.span(), "`package` attribute used on non-struct type");
                self.valid = false;
            }
            Item::Mod(i) if i.attrs.iter().any(has_package_attribute) => {
                emit_error!(i.span(), "`package` attribute used on non-struct type");
                self.valid = false;
            }
            Item::Static(i) if i.attrs.iter().any(has_package_attribute) => {
                emit_error!(i.span(), "`package` attribute used on non-struct type");
                self.valid = false;
            }
            Item::Trait(i) if i.attrs.iter().any(has_package_attribute) => {
                emit_error!(i.span(), "`package` attribute used on non-struct type");
                self.valid = false;
            }
            Item::TraitAlias(i) if i.attrs.iter().any(has_package_attribute) => {
                emit_error!(i.span(), "`package` attribute used on non-struct type");
                self.valid = false;
            }
            Item::Type(i) if i.attrs.iter().any(has_package_attribute) => {
                emit_error!(i.span(), "`package` attribute used on non-struct type");
                self.valid = false;
            }
            Item::Union(i) if i.attrs.iter().any(has_package_attribute) => {
                emit_error!(i.span(), "`package` attribute used on non-struct type");
                self.valid = false;
            }
            Item::Use(i) if i.attrs.iter().any(has_package_attribute) => {
                emit_error!(i.span(), "`package` attribute used on non-struct type");
                self.valid = false;
            }
            Item::Verbatim(_) => {}
            _ => {}
        }
    }
}

#[derive(Default)]
struct ImplAccumulator<'ast> {
    impls: Vec<&'ast ItemImpl>,
}

impl<'ast> Visit<'ast> for ImplAccumulator<'ast> {
    fn visit_item_impl(&mut self, node: &'ast ItemImpl) {
        self.impls.push(node);
    }
}

enum StructDeclarationKind {
    // structs with `package` attrib and impl
    Bridged,
    // structs with `package` attrib but no impl
    UnImpl,
    // structs without `package` attrib but with impl
    UnAttrib,
    // structs without `package` attrib and no impl
    Bare,
}

struct StructDeclVisitor<'ast> {
    module_structs: Vec<(&'ast ItemStruct, StructDeclarationKind)>,
    // all module impls
    module_impls: Vec<&'ast ItemImpl>,
}

impl<'ast> StructDeclVisitor<'ast> {
    fn new(module_impls: Vec<&'ast ItemImpl>) -> Self {
        StructDeclVisitor {
            module_structs: Vec::new(),
            module_impls,
        }
    }
}

impl<'ast> Visit<'ast> for StructDeclVisitor<'ast> {
    fn visit_item_struct(&mut self, node: &'ast ItemStruct) {
        let struct_name = node.ident.to_string();
        let has_package_attrib = node
            .attrs
            .iter()
            .any(|a| a.path().segments.first().unwrap().ident == "package");
        let has_impl = self
            .module_impls
            .iter()
            .filter_map(|i| match &*i.self_ty {
                Type::Path(p) => Some(p.path.segments.last().unwrap().ident.to_string()),
                _ => None,
            })
            .any(|s| s == struct_name);

        let declaration_kind = match (has_package_attrib, has_impl) {
            (true, true) => StructDeclarationKind::Bridged,
            (true, false) => StructDeclarationKind::UnImpl,
            (false, true) => StructDeclarationKind::UnAttrib,
            (false, false) => StructDeclarationKind::Bare,
        };

        self.module_structs.push((node, declaration_kind))
    }
}

pub(crate) struct JNIBridgeModule {
    pub(crate) module_decl: ItemMod,
    pub(crate) package_map: BTreeMap<String, Option<JavaPath>>,
}

impl Parse for JNIBridgeModule {
    fn parse(input: &ParseBuffer) -> Result<Self> {
        let mut valid_input;
        let module_decl: ItemMod = input.parse().map_err(|e| {
            Error::new(
                e.span(),
                "`bridge` attribute is supported on mod items only",
            )
        })?;

        let mut attribute_checker = AttribItemChecker::new();
        attribute_checker.visit_item_mod(&module_decl);
        valid_input = attribute_checker.valid;

        let mut impl_visitor = ImplAccumulator::default();
        impl_visitor.visit_item_mod(&module_decl);

        let mut mod_visitor = StructDeclVisitor::new(impl_visitor.impls);
        mod_visitor.visit_item_mod(&module_decl);

        let bridged_structs: Vec<_> = mod_visitor.module_structs.into_iter()
            .filter_map(|(struct_item, decl_kind)| {
                match decl_kind {
                    StructDeclarationKind::Bridged => Some(struct_item),
                    StructDeclarationKind::UnImpl => {
                        emit_warning!(struct_item, "ignoring struct without declared methods"; help = "add methods using an `impl` block");
                        None
                    }
                    StructDeclarationKind::UnAttrib => {
                        emit_error!(struct_item, "struct without required `package` attribute");
                        valid_input = false;
                        None
                    }
                    StructDeclarationKind::Bare => {
                        emit_warning!(struct_item, "ignoring struct with no `package` attribute and no implementation";
                            help = struct_item.span() => "add a #[package(...)] attribute";
                            note = "structs with declared methods require package attribute for correct translation");
                        None
                    }
                }
            })
            .collect();

        let structs_idents: Vec<_> = bridged_structs.iter().map(|s| &s.ident).collect();
        let bridged_impls: Vec<_> = mod_visitor
            .module_impls
            .iter()
            .filter_map(|item_impl| match &*item_impl.self_ty {
                Type::Path(p) => structs_idents
                    .iter()
                    .position(|id| *id == &p.path.segments.last().unwrap().ident)
                    .map(|pos| (bridged_structs[pos], *item_impl)),
                _ => None,
            })
            .map(|(s, i)| (s.clone(), i.clone()))
            .collect();

        mod_visitor
            .module_impls
            .into_iter()
            .filter(|i| {
                if let Type::Path(p) = &*i.self_ty {
                    let impl_struct_name = p.path.segments.last().unwrap().ident.to_string();
                    let has_generics = i
                        .generics
                        .params
                        .iter()
                        .filter_map(|g| match g {
                            GenericParam::Type(t) => Some(&t.ident),
                            _ => None,
                        })
                        .next()
                        .is_some();

                    !bridged_impls
                        .iter()
                        .map(|(_, i)| i)
                        .filter_map(|i| {
                            // *Very* conservative check to avoid hassles with checking struct name in where clauses
                            // Should refactor into something proper or just delete this
                            if !has_generics {
                                match &*i.self_ty {
                                    Type::Path(p) => {
                                        Some(p.path.segments.last().unwrap().ident.to_string())
                                    }
                                    _ => None,
                                }
                            } else {
                                Some(impl_struct_name.clone()) // ignore this impl item
                            }
                        })
                        .any(|struct_name| struct_name == impl_struct_name)
                } else {
                    false
                }
            })
            .for_each(|lone_impl| {
                emit_error!(
                    lone_impl,
                    "impl declared without corresponding struct \"{}\"",
                    lone_impl.self_ty.to_token_stream()
                );
                valid_input = false;
            });

        let package_map: BTreeMap<String, Option<JavaPath>> = bridged_structs
            .iter()
            .map(|s| {
                let name = s.ident.to_string();
                let package_path = s
                    .attrs
                    .iter()
                    .filter(|a| a.path().segments.last().unwrap().ident == "package")
                    .map(|a| a.parse_args::<JavaPath>().unwrap())
                    .next()
                    .unwrap();

                let package = Some(package_path);

                (name, package)
            })
            .collect();

        if !valid_input {
            Err(Error::new(
                module_decl.span(),
                "`bridge` macro expansion failed due to previous errors",
            ))
        } else {
            Ok(JNIBridgeModule {
                module_decl,
                package_map,
            })
        }
    }
}



================================================
FILE: robusta-codegen/src/derive/convert.rs
================================================
use std::collections::HashMap;

use crate::derive::utils::generic_params_to_args;
use crate::transformation::JavaPath;
use proc_macro2::{Ident, TokenStream};
use proc_macro_error::{abort, emit_error, emit_warning};
use quote::{quote, quote_spanned, ToTokens};
use syn::spanned::Spanned;
use syn::{
    AngleBracketedGenericArguments, Data, DataStruct, DeriveInput, Field, GenericArgument,
    GenericParam, Generics, LifetimeParam, PathArguments, Type, TypePath,
};

struct TraitAutoDeriveData {
    instance_field_type_assertion: TokenStream,
    impl_target: Ident,
    classpath_path: String,
    generics: Generics,
    instance_ident: Ident,
    generic_args: AngleBracketedGenericArguments,
    data_fields: Vec<Field>,
    class_fields: Vec<Field>,
}

pub(crate) fn into_java_value_macro_derive(input: DeriveInput) -> TokenStream {
    let input_span = input.span();
    match into_java_value_macro_derive_impl(input) {
        Ok(t) => t,
        Err(_) => quote_spanned! { input_span => },
    }
}

fn into_java_value_macro_derive_impl(input: DeriveInput) -> syn::Result<TokenStream> {
    let TraitAutoDeriveData {
        instance_field_type_assertion,
        impl_target,
        generics,
        instance_ident,
        generic_args,
        ..
    } = get_trait_impl_components("IntoJavaValue", input);

    Ok(quote! {
        #instance_field_type_assertion

        #[automatically_derived]
        impl#generics ::robusta_jni::convert::IntoJavaValue<'env> for #impl_target#generic_args {
            type Target = ::robusta_jni::jni::objects::JObject<'env>;

            fn into(self, env: &::robusta_jni::jni::JNIEnv<'env>) -> Self::Target {
                ::robusta_jni::convert::IntoJavaValue::into(self, env)
            }
        }

        #[automatically_derived]
        impl#generics ::robusta_jni::convert::IntoJavaValue<'env> for &#impl_target#generic_args {
            type Target = ::robusta_jni::jni::objects::JObject<'env>;

            fn into(self, env: &::robusta_jni::jni::JNIEnv<'env>) -> Self::Target {
                self.#instance_ident.as_obj()
            }
        }

        #[automatically_derived]
        impl#generics ::robusta_jni::convert::IntoJavaValue<'env> for &mut #impl_target#generic_args {
            type Target = ::robusta_jni::jni::objects::JObject<'env>;

            fn into(self, env: &::robusta_jni::jni::JNIEnv<'env>) -> Self::Target {
                ::robusta_jni::convert::IntoJavaValue::into(self, env)
            }
        }
    })
}

pub(crate) fn tryinto_java_value_macro_derive(input: DeriveInput) -> TokenStream {
    let input_span = input.span();
    match tryinto_java_value_macro_derive_impl(input) {
        Ok(t) => t,
        Err(_) => quote_spanned! { input_span => },
    }
}

fn tryinto_java_value_macro_derive_impl(input: DeriveInput) -> syn::Result<TokenStream> {
    let TraitAutoDeriveData {
        instance_field_type_assertion,
        impl_target,
        generics,
        instance_ident,
        generic_args,
        ..
    } = get_trait_impl_components("TryIntoJavaValue", input);

    Ok(quote! {
        #instance_field_type_assertion

        #[automatically_derived]
        impl#generics ::robusta_jni::convert::TryIntoJavaValue<'env> for #impl_target#generic_args {
            type Target = ::robusta_jni::jni::objects::JObject<'env>;

            fn try_into(self, env: &::robusta_jni::jni::JNIEnv<'env>) -> ::robusta_jni::jni::errors::Result<Self::Target> {
                ::robusta_jni::convert::TryIntoJavaValue::try_into(self, env)
            }
        }

        #[automatically_derived]
        impl#generics ::robusta_jni::convert::TryIntoJavaValue<'env> for &#impl_target#generic_args {
            type Target = ::robusta_jni::jni::objects::JObject<'env>;

            fn try_into(self, env: &::robusta_jni::jni::JNIEnv<'env>) -> ::robusta_jni::jni::errors::Result<Self::Target> {
                Ok(self.#instance_ident.as_obj())
            }
        }

        #[automatically_derived]
        impl#generics ::robusta_jni::convert::TryIntoJavaValue<'env> for &mut #impl_target#generic_args {
            type Target = ::robusta_jni::jni::objects::JObject<'env>;

            fn try_into(self, env: &::robusta_jni::jni::JNIEnv<'env>) -> ::robusta_jni::jni::errors::Result<Self::Target> {
                ::robusta_jni::convert::TryIntoJavaValue::try_into(self, env)
            }
        }
    })
}

pub(crate) fn from_java_value_macro_derive(input: DeriveInput) -> TokenStream {
    let input_span = input.span();
    match from_java_value_macro_derive_impl(input) {
        Ok(t) => t,
        Err(_) => quote_spanned! { input_span => },
    }
}

fn from_java_value_macro_derive_impl(input: DeriveInput) -> syn::Result<TokenStream> {
    let TraitAutoDeriveData {
        instance_field_type_assertion,
        impl_target,
        classpath_path,
        generics,
        instance_ident,
        generic_args,
        data_fields,
        class_fields,
    } = get_trait_impl_components("FromJavaValue", input);

    let data_fields_struct_init: Vec<_> = data_fields
        .iter()
        .map(|f| f.ident.as_ref().unwrap())
        .collect();
    let data_fields_env_init: Vec<_> = data_fields.iter().map(|f| {
        let field_ident = f.ident.as_ref().unwrap();
        let field_name = field_ident.to_string();
        let field_type = &f.ty;
        let field_type_sig = quote_spanned! { field_type.span() =>
            <#field_type as Signature>::SIG_TYPE
        };
        quote_spanned! { f.span() =>
            let #field_ident: #field_type = ::robusta_jni::convert::FromJavaValue::from(::core::convert::TryInto::try_into(::robusta_jni::convert::JValueWrapper::from(env.get_field(source, #field_name, #field_type_sig).unwrap())).unwrap(), env);
        }
    }).collect();

    let class_fields_struct_init: Vec<_> = class_fields
        .iter()
        .map(|f| f.ident.as_ref().unwrap())
        .collect();
    let class_fields_env_init: Vec<_> = class_fields
        .iter()
        .map(|f| {
            let field_ident = f.ident.as_ref().unwrap();
            let field_name = field_ident.to_string();
            let field_type = &f.ty;

            quote_spanned! { f.span() =>
                let #field_ident: #field_type = ::robusta_jni::convert::Field::field_from(source,
                    #classpath_path,
                    #field_name,
                    env);
            }
        })
        .collect();

    Ok(quote! {
        #instance_field_type_assertion

        #[automatically_derived]
        impl#generics ::robusta_jni::convert::FromJavaValue<'env, 'borrow> for #impl_target#generic_args {
            type Source = ::robusta_jni::jni::objects::JObject<'env>;

            fn from(source: Self::Source, env: &'borrow ::robusta_jni::jni::JNIEnv<'env>) -> Self {
                #(#data_fields_env_init)*
                #(#class_fields_env_init)*

                Self {
                    #instance_ident: ::robusta_jni::jni::objects::AutoLocal::new(env, source),
                    #(#data_fields_struct_init),*
                    #(#class_fields_struct_init),*
                }
            }
        }
    })
}

pub(crate) fn tryfrom_java_value_macro_derive(input: DeriveInput) -> TokenStream {
    let input_span = input.span();
    match tryfrom_java_value_macro_derive_impl(input) {
        Ok(t) => t,
        Err(_) => quote_spanned! { input_span => },
    }
}

fn tryfrom_java_value_macro_derive_impl(input: DeriveInput) -> syn::Result<TokenStream> {
    let TraitAutoDeriveData {
        instance_field_type_assertion,
        impl_target,
        classpath_path,
        generics,
        instance_ident,
        generic_args,
        data_fields,
        class_fields,
    } = get_trait_impl_components("FromJavaValue", input);

    let data_fields_struct_init: Vec<_> = data_fields
        .iter()
        .map(|f| f.ident.as_ref().unwrap())
        .collect();
    let data_fields_env_init: Vec<_> = data_fields.iter().map(|f| {
        let field_ident = f.ident.as_ref().unwrap();
        let field_name = field_ident.to_string();
        let field_type = &f.ty;
        let field_type_sig = quote_spanned! { field_type.span() =>
            <#field_type as Signature>::SIG_TYPE
        };
        quote_spanned! { f.span() =>
            let #field_ident: #field_type = ::robusta_jni::convert::TryFromJavaValue::try_from(::core::convert::TryInto::try_into(::robusta_jni::convert::JValueWrapper::from(env.get_field(source, #field_name, #field_type_sig)?))?, env)?;
        }
    }).collect();

    let class_fields_struct_init: Vec<_> = class_fields
        .iter()
        .map(|f| f.ident.as_ref().unwrap())
        .collect();
    let class_fields_env_init: Vec<_> = class_fields.iter().map(|f| {
        let field_ident = f.ident.as_ref().unwrap();
        let field_name = field_ident.to_string();
        let field_type = &f.ty;

        quote_spanned! { f.span() =>
            let #field_ident: #field_type = ::robusta_jni::convert::Field::field_try_from(source,
                #classpath_path,
                #field_name,
                env)?;
        }
    }).collect();

    Ok(quote! {
        #instance_field_type_assertion

        #[automatically_derived]
        impl#generics ::robusta_jni::convert::TryFromJavaValue<'env, 'borrow> for #impl_target#generic_args {
            type Source = ::robusta_jni::jni::objects::JObject<'env>;

            fn try_from(source: Self::Source, env: &'borrow ::robusta_jni::jni::JNIEnv<'env>) -> ::robusta_jni::jni::errors::Result<Self> {
                #(#data_fields_env_init)*
                #(#class_fields_env_init)*

                Ok(Self {
                    #instance_ident: ::robusta_jni::jni::objects::AutoLocal::new(env, source),
                    #(#data_fields_struct_init),*
                    #(#class_fields_struct_init),*
                })
            }
        }
    })
}

fn get_trait_impl_components(trait_name: &str, input: DeriveInput) -> TraitAutoDeriveData {
    let input_span = input.span();
    let input_ident = &input.ident;

    match input.data {
        Data::Struct(DataStruct { fields, .. }) => {
            let package_attr = input.attrs.iter().find(|a| {
                a.path().get_ident().map(ToString::to_string).as_deref() == Some("package")
            });
            if package_attr.is_none() {
                abort!(input_span, "missing `#[package]` attribute")
            }

            let classpath_path = package_attr
                .unwrap()
                .parse_args()
                .map(|p: JavaPath| p.to_classpath_path())
                .map(|s| {
                    let mut s = s.clone();
                    if !s.is_empty() {
                        s.push('/');
                    }
                    s.push_str(&input_ident.to_string());
                    s
                })
                .unwrap_or_else(|_| {
                    emit_error!(package_attr, "invalid Java class path");
                    "".to_string()
                });

            let lifetimes: HashMap<String, &LifetimeParam> = input
                .generics
                .params
                .iter()
                .filter_map(|g| match g {
                    GenericParam::Lifetime(l) => Some(l),
                    _ => None,
                })
                .map(|l| (l.lifetime.ident.to_string(), l))
                .collect();

            match (lifetimes.get("env"), lifetimes.get("borrow")) {
                (Some(env_lifetime), Some(borrow_lifetime)) => {
                    if !env_lifetime
                        .bounds
                        .iter()
                        .any(|l| *l == borrow_lifetime.lifetime)
                    {
                        emit_error!(env_lifetime, "`'env` lifetime must have a `'borrow` lifetime bound";
                                    help = "try adding `'env: 'borrow`")
                    }
                }
                _ => emit_error!(
                    input_span,
                    "deriving struct must have `'env` and `'borrow` lifetime parameters"
                ),
            }

            let instance_fields: Vec<_> = fields
                .iter()
                .filter_map(|f| {
                    let attr = f.attrs.iter().find(|a| {
                        a.path().get_ident().map(|i| i.to_string()).as_deref() == Some("instance")
                    });
                    attr.map(|a| (f, a))
                })
                .collect();

            let class_fields: Vec<_> = fields
                .iter()
                .filter(|f| {
                    let attr = f.attrs.iter().find(|a| {
                        a.path().get_ident().map(|i| i.to_string()).as_deref() == Some("field")
                    });
                    attr.is_some()
                })
                .collect();

            if instance_fields.len() > 1 {
                emit_error!(
                    input_span,
                    "cannot have more than one `#[instance]` attribute"
                )
            }

            let instance_field_data = instance_fields.get(0);

            match instance_field_data {
                None => abort!(input_span, "missing `#[instance] field attribute"),
                Some((instance, attr)) => {
                    if attr
                        .meta
                        .require_list()
                        .is_ok_and(|meta_list| !meta_list.tokens.is_empty())
                    {
                        emit_warning!(
                            attr.to_token_stream(),
                            "`#[instance]` attribute doesn't have any arguments"
                        )
                    }

                    let ty = {
                        let mut t = instance.ty.clone();
                        if let Type::Path(TypePath { path, .. }) = &mut t {
                            path.segments.iter_mut().for_each(|s| {
                                if let PathArguments::AngleBracketed(a) = &mut s.arguments {
                                    a.args.iter_mut().for_each(|g| {
                                        if let GenericArgument::Lifetime(l) = g {
                                            l.ident = Ident::new("static", l.span());
                                        }
                                    })
                                }
                            });
                        }

                        t
                    };

                    let instance_field_type_assertion = quote_spanned! { ty.span() =>
                        ::robusta_jni::assert_type_eq_all!(#ty, ::robusta_jni::jni::objects::AutoLocal<'static, 'static>);
                    };

                    let generics = input.generics;
                    let instance_span = instance.span();
                    let instance_ident = instance.ident.as_ref().unwrap_or_else(|| {
                        abort!(instance_span, "instance field must have a name")
                    });

                    let generic_args = generic_params_to_args(generics.clone());

                    let data_fields: Vec<_> = fields
                        .iter()
                        .filter(|f| {
                            f.ident.as_ref() != Some(instance_ident)
                                && class_fields.iter().all(|g| g != f)
                        })
                        .cloned()
                        .collect();

                    TraitAutoDeriveData {
                        instance_field_type_assertion,
                        impl_target: input.ident,
                        classpath_path,
                        generics,
                        instance_ident: instance_ident.clone(),
                        generic_args,
                        data_fields,
                        class_fields: class_fields.into_iter().cloned().collect(),
                    }
                }
            }
        }
        _ => abort!(
            input,
            "`{}` auto-derive implemented for structs only",
            trait_name
        ),
    }
}



================================================
FILE: robusta-codegen/src/derive/mod.rs
================================================
pub(crate) mod convert;
pub(crate) mod signature;
mod utils;



================================================
FILE: robusta-codegen/src/derive/signature.rs
================================================
use proc_macro2::TokenStream;
use proc_macro_error::abort;
use quote::{quote, quote_spanned};
use syn::spanned::Spanned;
use syn::{Data, DataStruct, DeriveInput};

use crate::transformation::JavaPath;

use super::utils::generic_params_to_args;

pub(crate) fn signature_macro_derive(input: DeriveInput) -> TokenStream {
    let input_span = input.span();
    match signature_macro_derive_impl(input) {
        Ok(t) => t,
        Err(_) => quote_spanned! { input_span => },
    }
}

fn signature_macro_derive_impl(input: DeriveInput) -> syn::Result<TokenStream> {
    let input_span = input.span();

    match input.data {
        Data::Struct(DataStruct { .. }) => {
            let package_attr = input.attrs.iter().find(|a| {
                a.path().get_ident().map(ToString::to_string).as_deref() == Some("package")
            });

            match package_attr {
                None => abort!(input_span, "missing `#[package()]` attribute"),
                Some(attr) => {
                    let struct_name = input.ident;
                    let package = attr.parse_args::<JavaPath>()?;
                    let package_str = {
                        let mut s = package.to_classpath_path();
                        if !s.is_empty() {
                            s.push('/')
                        }
                        s
                    };

                    let signature = [
                        "L",
                        package_str.as_str(),
                        struct_name.to_string().as_str(),
                        ";",
                    ]
                    .join("");
                    let generics = input.generics.clone();
                    let generic_args = generic_params_to_args(input.generics);

                    Ok(quote! {
                        #[automatically_derived]
                        impl#generics ::robusta_jni::convert::Signature for #struct_name#generic_args {
                            const SIG_TYPE: &'static str = #signature;
                        }

                        #[automatically_derived]
                        impl#generics ::robusta_jni::convert::Signature for &#struct_name#generic_args {
                            const SIG_TYPE: &'static str = <#struct_name as ::robusta_jni::convert::Signature>::SIG_TYPE;
                        }

                        #[automatically_derived]
                        impl#generics ::robusta_jni::convert::Signature for &mut #struct_name#generic_args {
                            const SIG_TYPE: &'static str = <#struct_name as ::robusta_jni::convert::Signature>::SIG_TYPE;
                        }
                    })
                }
            }
        }
        _ => abort!(
            input_span,
            "`Signature` auto-derive implemented for structs only"
        ),
    }
}



================================================
FILE: robusta-codegen/src/derive/utils.rs
================================================
use syn::parse_quote;
use syn::punctuated::Punctuated;
use syn::spanned::Spanned;
use syn::Token;
use syn::{
    AngleBracketedGenericArguments, ConstParam, GenericArgument, GenericParam, Generics, TypeParam,
};

pub(crate) fn generic_params_to_args(generics: Generics) -> AngleBracketedGenericArguments {
    let args: Punctuated<GenericArgument, Token![,]> = generics
        .params
        .iter()
        .map(|g| match g {
            GenericParam::Type(TypeParam { ident, .. }) => {
                GenericArgument::Type(parse_quote! { #ident })
            }
            GenericParam::Lifetime(l) => GenericArgument::Lifetime(l.lifetime.clone()),
            GenericParam::Const(ConstParam { ident, .. }) => {
                GenericArgument::Const(parse_quote! { #ident })
            }
        })
        .collect();

    AngleBracketedGenericArguments {
        colon2_token: None,
        lt_token: generics
            .lt_token
            .unwrap_or_else(|| Token![<](generics.span())),
        args,
        gt_token: generics
            .gt_token
            .unwrap_or_else(|| Token![>](generics.span())),
    }
}



================================================
FILE: robusta-codegen/src/transformation/context.rs
================================================
use crate::transformation::JavaPath;
use syn::{LifetimeParam, Path};

#[derive(Clone)]
pub(crate) struct StructContext {
    pub(crate) struct_type: Path,
    pub(crate) struct_name: String,
    pub(crate) struct_lifetimes: Vec<LifetimeParam>,
    pub(crate) package: Option<JavaPath>,
}



================================================
FILE: robusta-codegen/src/transformation/exported.rs
================================================
use std::collections::HashSet;

use proc_macro2::Ident;
use proc_macro_error::{emit_error, emit_warning};
use quote::ToTokens;
use syn::fold::Fold;
use syn::punctuated::Punctuated;
use syn::spanned::Spanned;
use syn::token::Extern;
use syn::Lifetime;
use syn::Token;
use syn::{parse_quote, GenericParam, Generics, LifetimeParam, TypeTuple};
use syn::{
    Abi, Block, Expr, FnArg, ImplItemFn, LitStr, Pat, PatIdent, PatType, ReturnType, Signature,
    Type, Visibility,
};

use crate::transformation::context::StructContext;
use crate::transformation::utils::get_call_type;
use crate::transformation::{CallType, FreestandingTransformer, SafeParams};
use crate::utils::{get_abi, get_env_arg, is_self_method};
use std::iter::FromIterator;

pub struct ExportedMethodTransformer<'ctx> {
    pub(crate) struct_context: &'ctx StructContext,
}

impl<'ctx> Fold for ExportedMethodTransformer<'ctx> {
    fn fold_impl_item_fn(&mut self, node: ImplItemFn) -> ImplItemFn {
        let abi = get_abi(&node.sig);
        match (&node.vis, &abi.as_deref()) {
            (Visibility::Public(_), Some("jni")) => {
                let call_type_attribute = get_call_type(&node)
                    .map(|c| c.call_type)
                    .unwrap_or(CallType::Safe(None));

                let mut jni_method_transformer =
                    ExternJNIMethodTransformer::new(self.struct_context, call_type_attribute);
                jni_method_transformer.fold_impl_item_fn(node)
            }
            _ => node,
        }
    }
}

struct ExternJNIMethodTransformer<'ctx> {
    struct_context: &'ctx StructContext,
    call_type: CallType,
}

impl<'ctx> ExternJNIMethodTransformer<'ctx> {
    fn new(struct_context: &'ctx StructContext, call_type: CallType) -> Self {
        ExternJNIMethodTransformer {
            struct_context,
            call_type,
        }
    }
}

impl<'ctx> Fold for ExternJNIMethodTransformer<'ctx> {
    fn fold_impl_item_fn(&mut self, node: ImplItemFn) -> ImplItemFn {
        let jni_signature = JNISignature::new(
            node.sig.clone(),
            &self.struct_context,
            self.call_type.clone(),
        );

        let transformed_jni_signature = jni_signature.transformed_signature();
        let method_call = jni_signature.signature_call();

        let new_block: Block = match &self.call_type {
            CallType::Unchecked { .. } => {
                parse_quote_spanned! { node.span() => {
                    ::robusta_jni::convert::IntoJavaValue::into(#method_call, &env)
                }}
            }

            CallType::Safe(exception_details) => {
                let outer_call_inputs = {
                    let mut inputs: Punctuated<Expr, Token![,]> = jni_signature
                        .args_iter()
                        .map(|p| -> Expr {
                            let PatType { pat, .. } = p;

                            match &**pat {
                                Pat::Ident(PatIdent { ident, .. }) => {
                                    parse_quote_spanned!(ident.span() => #ident)
                                }
                                _ => panic!("Non-identifier argument pattern in function"),
                            }
                        })
                        .collect();

                    inputs.push(parse_quote!(&env));
                    inputs
                };
                let outer_signature = {
                    let mut s = transformed_jni_signature.clone();
                    s.ident = Ident::new("outer", s.ident.span());

                    s.inputs.iter_mut().for_each(|i| {
                        if let FnArg::Typed(PatType { pat, .. }) = i {
                            if let Pat::Ident(PatIdent { mutability, .. }) = pat.as_mut() {
                                *mutability = None
                            }
                        }
                    });

                    s.inputs.push(FnArg::Typed(PatType {
                        attrs: vec![],
                        pat: Box::new(Pat::Ident(PatIdent {
                            attrs: vec![],
                            by_ref: None,
                            mutability: None,
                            ident: Ident::new("env", s.inputs.span()),
                            subpat: None,
                        })),
                        colon_token: Token![:](s.inputs.span()),
                        ty: Box::new(parse_quote! { &'borrow ::robusta_jni::jni::JNIEnv<'env> }),
                    }));

                    let outer_signature_span = s.span();
                    let outer_output_type: Type = match s.output {
                        ReturnType::Default => parse_quote!(()),
                        ReturnType::Type(_, ty) => *ty,
                    };

                    s.output = ReturnType::Type(
                        Token![->](outer_signature_span),
                        Box::new(
                            parse_quote_spanned!(outer_output_type.span() => ::robusta_jni::jni::errors::Result<#outer_output_type>),
                        ),
                    );
                    s.abi = None;
                    s
                };

                let (default_exception_class, default_message) = (
                    "java.lang.RuntimeException".parse().unwrap(),
                    "JNI call error!",
                );
                let (exception_class, message) = match exception_details {
                    Some(SafeParams {
                        exception_class,
                        message,
                    }) => {
                        let exception_class_result =
                            exception_class.as_ref().unwrap_or(&default_exception_class);
                        let message_result = message.as_deref().unwrap_or(default_message);

                        (exception_class_result, message_result)
                    }
                    None => (&default_exception_class, default_message),
                };

                let exception_classpath_path = exception_class.to_classpath_path();

                parse_quote_spanned! { node.span() => {
                    #outer_signature {
                        ::robusta_jni::convert::TryIntoJavaValue::try_into(#method_call, &env)
                    }

                    match outer(#outer_call_inputs) {
                        Ok(result) => result,
                        Err(e) => {
                            let r = env.throw_new(#exception_classpath_path, format!("{}. Cause: {}", #message, e));

                            if let Err(e) = r {
                                println!("Error while throwing Java exception: {}", e);
                            }

                            /* We never hand out Rust references and the object returned is ignored
                             * by the JVM, so it should be safe to just return zeroed memory.
                             * Also, all primitives have a valid zero representation and because objects
                             * are represented as pointers this should not have any unsafe side effects.
                             * (Uninitialized memory would probably work as well)
                             */
                            unsafe { ::std::mem::zeroed() }
                        }
                    }
                }}
            }
        };

        let no_mangle = parse_quote! { #[no_mangle] };
        let impl_item_attributes = {
            let mut attributes = node.attrs.clone();
            attributes.push(no_mangle);

            let discarded_known_attributes: HashSet<&str> = {
                let mut h = HashSet::new();
                h.insert("call_type");
                h
            };

            attributes
                .into_iter()
                .filter(|a| {
                    !discarded_known_attributes
                        .contains(&a.path().segments.to_token_stream().to_string().as_str())
                })
                .collect()
        };

        let node_span = node.span();
        ImplItemFn {
            attrs: impl_item_attributes,
            vis: Visibility::Public(Token![pub](node_span)),
            defaultness: node.defaultness,
            sig: self.fold_signature(node.sig),
            block: new_block,
        }
    }

    /// Transform original signature in JNI-ready one, including JClass and JNIEnv parameters into the function signature.
    fn fold_signature(&mut self, node: Signature) -> Signature {
        let jni_signature =
            JNISignature::new(node.clone(), &self.struct_context, self.call_type.clone());

        let mut sig = jni_signature.transformed_signature;

        if sig.ident.to_string().contains('_') {
            emit_error!(sig.ident, "JNI methods cannot contain `_` character");
        }

        let jni_method_name = {
            let snake_case_package = self
                .struct_context
                .package
                .as_ref()
                .map(|s| s.to_snake_case())
                .unwrap_or_else(|| "".into());

            [
                "Java",
                &snake_case_package,
                &self.struct_context.struct_name,
                &sig.ident.to_string(),
            ]
            .iter()
            .filter(|s| !s.is_empty())
            .map(|s| s.to_owned())
            .collect::<Vec<_>>()
            .join("_")
        };

        sig.inputs = {
            let mut res = Punctuated::new();
            res.push(parse_quote!(env: ::robusta_jni::jni::JNIEnv<'env>));

            if !is_self_method(&node) {
                res.push(parse_quote!(class: ::robusta_jni::jni::objects::JClass));
            }

            res.extend(sig.inputs);
            res
        };

        sig.ident = Ident::new(&jni_method_name, sig.ident.span());
        sig.abi = Some(Abi {
            extern_token: Extern { span: sig.span() },
            name: Some(LitStr::new("system", sig.span())),
        });

        sig
    }
}

#[cfg(test)]
mod test {
    use std::str::FromStr;

    use proc_macro2::TokenStream;

    use super::*;
    use crate::transformation::JavaPath;

    fn setup_package(
        package: Option<JavaPath>,
        struct_name: String,
        method_name: String,
    ) -> ImplItemFn {
        let struct_name_token_stream = TokenStream::from_str(&struct_name).unwrap();
        let method_name_token_stream = TokenStream::from_str(&method_name).unwrap();

        let method: ImplItemFn =
            parse_quote! { pub extern "jni" fn #method_name_token_stream() {} };
        let struct_context = StructContext {
            struct_type: parse_quote! { #struct_name_token_stream },
            struct_name,
            struct_lifetimes: vec![],
            package,
        };
        let mut transformer = ExternJNIMethodTransformer {
            struct_context: &struct_context,
            call_type: CallType::Safe(None),
        };

        transformer.fold_impl_item_method(method)
    }

    #[test]
    fn jni_method_is_public() {
        let output = setup_package(None, "Foo".into(), "foo".into());
        assert!(matches!(output.vis, Visibility::Public(_)))
    }

    #[test]
    fn jni_method_follows_naming_scheme() {
        let output_no_package = setup_package(None, "Foo".into(), "foo".into());
        assert_eq!(
            output_no_package.sig.ident.to_string(),
            format!("Java_Foo_foo")
        );

        let output_with_package = setup_package(
            Some(JavaPath::from_str("com.bar.quux").unwrap()),
            "Foo".into(),
            "foo".into(),
        );
        assert_eq!(
            output_with_package.sig.ident.to_string(),
            format!("Java_com_bar_quux_Foo_foo")
        );
    }

    #[test]
    fn jni_method_has_no_mangle() {
        let output = setup_package(None, "Foo".into(), "foo".into());
        let no_mangle = parse_quote! { #[no_mangle] };
        assert!(output.attrs.contains(&no_mangle));
    }

    #[test]
    fn jni_method_has_system_abi() {
        let output = setup_package(None, "Foo".into(), "foo".into());
        assert_eq!(output.sig.abi.unwrap().name.unwrap().value(), "system")
    }

    fn setup_with_params(params: TokenStream, struct_name: String) -> ImplItemFn {
        let package = None;
        let method_name = "foo".to_string();
        let struct_name_token_stream = TokenStream::from_str(&struct_name).unwrap();
        let method_name_token_stream = TokenStream::from_str(&method_name).unwrap();

        let method: ImplItemFn = parse_quote! {
            pub extern "jni" fn #method_name_token_stream(#params) -> i32 {}
        };

        let struct_context = StructContext {
            struct_type: parse_quote! { #struct_name_token_stream },
            struct_name,
            struct_lifetimes: vec![],
            package,
        };
        let mut transformer = ExternJNIMethodTransformer {
            struct_context: &struct_context,
            call_type: CallType::Safe(None),
        };

        transformer.fold_impl_item_method(method)
    }

    #[test]
    fn static_method_params() {
        use quote::quote;

        let param_type_1: TokenStream = parse_quote! { i32 };
        let param_type_2: TokenStream = parse_quote! { FooBar };
        let output = setup_with_params(
            quote! { _1: #param_type_1, _2: #param_type_2 },
            "Foo".to_string(),
        );

        let env_type: Type = parse_quote! { ::robusta_jni::jni::JNIEnv<'env> };
        let class_type: Type = parse_quote! { ::robusta_jni::jni::objects::JClass };
        let conv_type_1: Type = parse_quote! { <#param_type_1 as ::robusta_jni::convert::TryFromJavaValue<'env, 'borrow>>::Source };
        let conv_type_2: Type = parse_quote! { <#param_type_2 as ::robusta_jni::convert::TryFromJavaValue<'env, 'borrow>>::Source };

        let args: &[FnArg] = &output.sig.inputs.into_iter().collect::<Vec<_>>();
        match args {
            [FnArg::Typed(PatType { ty: ty_env, .. }), FnArg::Typed(PatType { ty: ty_class, .. }), FnArg::Typed(PatType { ty: ty_1, .. }), FnArg::Typed(PatType { ty: ty_2, .. })] =>
            {
                assert_eq!(
                    ty_env.to_token_stream().to_string(),
                    env_type.to_token_stream().to_string()
                );
                assert_eq!(
                    ty_class.to_token_stream().to_string(),
                    class_type.to_token_stream().to_string()
                );
                assert_eq!(
                    ty_1.to_token_stream().to_string(),
                    conv_type_1.to_token_stream().to_string()
                );
                assert_eq!(
                    ty_2.to_token_stream().to_string(),
                    conv_type_2.to_token_stream().to_string()
                );
            }

            _ => assert!(false),
        }
    }

    #[test]
    fn self_method_params() {
        use quote::quote;

        let struct_name = "Foo".to_string();
        let struct_name_toks = TokenStream::from_str(&struct_name).unwrap();

        let param_type_1: TokenStream = parse_quote! { i32 };
        let param_type_2: TokenStream = parse_quote! { FooBar };
        let output = setup_with_params(
            quote! { self, _1: #param_type_1, _2: #param_type_2 },
            struct_name.clone(),
        );

        let env_type: Type = parse_quote! { ::robusta_jni::jni::JNIEnv<'env> };
        let self_conv_type: Type = parse_quote! { <#struct_name_toks as ::robusta_jni::convert::TryFromJavaValue<'env, 'borrow>>::Source };
        let conv_type_1: Type = parse_quote! { <#param_type_1 as ::robusta_jni::convert::TryFromJavaValue<'env, 'borrow>>::Source };
        let conv_type_2: Type = parse_quote! { <#param_type_2 as ::robusta_jni::convert::TryFromJavaValue<'env, 'borrow>>::Source };

        let args: &[FnArg] = &output.sig.inputs.into_iter().collect::<Vec<_>>();
        match args {
            [FnArg::Typed(PatType { ty: ty_env, .. }), FnArg::Typed(PatType { ty: ty_self, .. }), FnArg::Typed(PatType { ty: ty_1, .. }), FnArg::Typed(PatType { ty: ty_2, .. })] =>
            {
                assert_eq!(
                    ty_env.to_token_stream().to_string(),
                    env_type.to_token_stream().to_string()
                );
                assert_eq!(
                    ty_self.to_token_stream().to_string(),
                    self_conv_type.to_token_stream().to_string()
                );
                assert_eq!(
                    ty_1.to_token_stream().to_string(),
                    conv_type_1.to_token_stream().to_string()
                );
                assert_eq!(
                    ty_2.to_token_stream().to_string(),
                    conv_type_2.to_token_stream().to_string()
                );
            }

            _ => assert!(false),
        }
    }
}

struct JNISignatureTransformer {
    struct_freestanding_transformer: FreestandingTransformer,
    struct_lifetimes: Vec<LifetimeParam>,
    call_type: CallType,
}

impl JNISignatureTransformer {
    fn new(
        struct_freestanding_transformer: FreestandingTransformer,
        struct_lifetimes: Vec<LifetimeParam>,
        call_type: CallType,
    ) -> Self {
        JNISignatureTransformer {
            struct_freestanding_transformer,
            struct_lifetimes,
            call_type,
        }
    }

    fn transform_generics(&mut self, mut generics: Generics) -> Generics {
        let generics_span = generics.span();
        generics.params.extend(
            self.struct_lifetimes
                .iter()
                .cloned()
                .map(GenericParam::Lifetime),
        );

        let (env_lifetime, borrow_lifetime) = generics.params.iter_mut().fold((None, None), |acc, l| {
            match l {
                GenericParam::Lifetime(l) => {
                    if l.lifetime.ident == "env" {
                        if l.bounds.iter().any(|b| b.ident != "borrow") {
                            emit_warning!(l, "using JNI-reserved `'env` lifetime with non `'borrow` bounds";
                                note = "If you need to access to the lifetime of the `JNIEnv`, please use `'borrow` instead")
                        }

                        (Some(l), acc.1)
                    } else if l.lifetime.ident == "borrow" {
                        (acc.0, Some(l))
                    } else {
                        acc
                    }
                },
                _ => acc
            }
        });

        match (env_lifetime, borrow_lifetime) {
            (Some(_), Some(_)) => {}
            (Some(e), None) => {
                let borrow_lifetime_value = Lifetime {
                    apostrophe: generics_span,
                    ident: Ident::new("borrow", generics_span),
                };

                e.bounds.push(borrow_lifetime_value.clone());

                generics.params.push(GenericParam::Lifetime(LifetimeParam {
                    attrs: vec![],
                    lifetime: borrow_lifetime_value,
                    colon_token: None,
                    bounds: Default::default(),
                }))
            }
            (None, Some(l)) => {
                emit_error!(l, "Can't use JNI-reserved `'borrow` lifetime without accompanying `'env: 'borrow` lifetime";
                    help = "Add `'env: 'borrow` lifetime here")
            }
            (None, None) => {
                let borrow_lifetime_value = Lifetime {
                    apostrophe: generics_span,
                    ident: Ident::new("borrow", generics_span),
                };

                generics.params.push(GenericParam::Lifetime(LifetimeParam {
                    attrs: vec![],
                    lifetime: Lifetime {
                        apostrophe: generics_span,
                        ident: Ident::new("env", generics_span),
                    },
                    colon_token: None,
                    bounds: {
                        let mut p = Punctuated::new();
                        p.push(borrow_lifetime_value.clone());
                        p
                    },
                }));

                generics.params.push(GenericParam::Lifetime(LifetimeParam {
                    attrs: vec![],
                    lifetime: borrow_lifetime_value,
                    colon_token: None,
                    bounds: Default::default(),
                }))
            }
        }

        generics
    }
}

impl Fold for JNISignatureTransformer {
    fn fold_fn_arg(&mut self, arg: FnArg) -> FnArg {
        match self.struct_freestanding_transformer.fold_fn_arg(arg) {
            FnArg::Receiver(_) => panic!("Bug -- please report to library author. Found receiver input after freestanding conversion"),
            FnArg::Typed(mut t) => {
                let original_input_type = t.ty;

                let jni_conversion_type: Type = match self.call_type {
                    CallType::Safe(_) => parse_quote_spanned! { original_input_type.span() => <#original_input_type as ::robusta_jni::convert::TryFromJavaValue<'env, 'borrow>>::Source },
                    CallType::Unchecked { .. } => parse_quote_spanned! { original_input_type.span() => <#original_input_type as ::robusta_jni::convert::FromJavaValue<'env, 'borrow>>::Source },
                };

                if let Pat::Ident(PatIdent { mutability, .. }) = t.pat.as_mut() {
                    *mutability = None
                }

                FnArg::Typed(PatType {
                    attrs: t.attrs,
                    pat: t.pat,
                    colon_token: t.colon_token,
                    ty: Box::new(jni_conversion_type),
                })
            }
        }
    }

    fn fold_return_type(&mut self, return_type: ReturnType) -> ReturnType {
        match return_type {
            ReturnType::Default => return_type,
            ReturnType::Type(ref arrow, ref rtype) => match (&**rtype, self.call_type.clone()) {
                (Type::Path(p), CallType::Unchecked { .. }) => ReturnType::Type(
                    *arrow,
                    parse_quote_spanned! { p.span() => <#p as ::robusta_jni::convert::IntoJavaValue<'env>>::Target },
                ),

                (Type::Path(p), CallType::Safe(_)) => ReturnType::Type(
                    *arrow,
                    parse_quote_spanned! { p.span() => <#p as ::robusta_jni::convert::TryIntoJavaValue<'env>>::Target },
                ),

                (Type::Reference(r), CallType::Unchecked { .. }) => ReturnType::Type(
                    *arrow,
                    parse_quote_spanned! { r.span() => <#r as ::robusta_jni::convert::IntoJavaValue<'env>>::Target },
                ),

                (Type::Reference(r), CallType::Safe(_)) => ReturnType::Type(
                    *arrow,
                    parse_quote_spanned! { r.span() => <#r as ::robusta_jni::convert::TryIntoJavaValue<'env>>::Target },
                ),
                (Type::Tuple(TypeTuple { elems, .. }), _) if elems.is_empty() => {
                    ReturnType::Default
                }
                _ => {
                    emit_error!(return_type, "Only type or type paths are permitted as type ascriptions in function params");
                    return_type
                }
            },
        }
    }

    fn fold_signature(&mut self, node: Signature) -> Signature {
        Signature {
            abi: node.abi.map(|a| self.fold_abi(a)),
            ident: self.fold_ident(node.ident),
            generics: self.transform_generics(node.generics),
            inputs: node
                .inputs
                .into_iter()
                .map(|f| self.fold_fn_arg(f))
                .collect(),
            variadic: node.variadic.map(|v| self.fold_variadic(v)),
            output: self.fold_return_type(node.output),
            ..node
        }
    }
}

struct JNISignature {
    transformed_signature: Signature,
    call_type: CallType,
    struct_name: String,
    self_method: bool,
    env_arg: Option<FnArg>,
}

impl JNISignature {
    fn new(
        signature: Signature,
        struct_context: &StructContext,
        call_type: CallType,
    ) -> JNISignature {
        let freestanding_transformer =
            FreestandingTransformer::new(struct_context.struct_type.clone());
        let mut jni_signature_transformer = JNISignatureTransformer::new(
            freestanding_transformer,
            struct_context.struct_lifetimes.clone(),
            call_type.clone(),
        );

        let self_method = is_self_method(&signature);
        let (transformed_signature, env_arg) = get_env_arg(signature);

        let transformed_signature = jni_signature_transformer.fold_signature(transformed_signature);

        JNISignature {
            transformed_signature,
            call_type,
            struct_name: struct_context.struct_name.clone(),
            self_method,
            env_arg,
        }
    }

    fn args_iter(&self) -> impl Iterator<Item = &PatType> {
        self.transformed_signature.inputs.iter()
            .map(|a| match a {
                FnArg::Receiver(_) => panic!("Bug -- please report to library author. Found receiver type in freestanding signature!"),
                FnArg::Typed(p) => p
            })
    }

    fn signature_call(&self) -> Expr {
        let method_call_inputs: Punctuated<Expr, Token![,]> = {
            let mut result: Vec<_> = self.args_iter()
                .map(|p| {
                    match p.pat.as_ref() {
                        Pat::Ident(PatIdent { ident, .. }) => {
                            let input_param: Expr = {
                                match self.call_type {
                                    CallType::Safe(_) => parse_quote_spanned! { ident.span() => ::robusta_jni::convert::TryFromJavaValue::try_from(#ident, &env)? },
                                    CallType::Unchecked { .. } => parse_quote_spanned! { ident.span() => ::robusta_jni::convert::FromJavaValue::from(#ident, &env) }
                                }
                            };
                            input_param
                        }
                        _ => panic!("Bug -- please report to library author. Found non-ident FnArg pattern")
                    }
            }).collect();

            if let Some(ref e) = self.env_arg {
                // because `self` is kept in the transformed JNI signature, if this is a `self` method we put `env` *after* self, otherwise the env parameter must be first
                let idx = if self.self_method { 1 } else { 0 };
                let env_span = e.span();
                result.insert(idx, parse_quote_spanned!(env_span => &env));
            }

            Punctuated::from_iter(result.into_iter())
        };

        let signature_span = self.transformed_signature.span();
        let struct_name = Ident::new(&self.struct_name, signature_span);
        let method_name = self.transformed_signature.ident.clone();

        parse_quote_spanned! { signature_span =>
            #struct_name::#method_name(#method_call_inputs)
        }
    }

    fn transformed_signature(&self) -> &Signature {
        &self.transformed_signature
    }
}



================================================
FILE: robusta-codegen/src/transformation/imported.rs
================================================
use inflector::cases::camelcase::to_camel_case;
use proc_macro2::{TokenStream, TokenTree};
use proc_macro_error::{abort, emit_error, emit_warning};
use quote::{quote_spanned, ToTokens};
use syn::fold::Fold;
use syn::spanned::Spanned;
use syn::{parse_quote, GenericArgument, PathArguments, Type, TypePath};
use syn::{FnArg, ImplItemFn, Lit, Pat, PatIdent, ReturnType, Signature};

use crate::transformation::context::StructContext;
use crate::transformation::utils::get_call_type;
use crate::transformation::{CallType, CallTypeAttribute, SafeParams};
use crate::utils::{get_abi, get_class_arg_if_any, get_env_arg, is_self_method};
use std::collections::HashSet;

pub struct ImportedMethodTransformer<'ctx> {
    pub(crate) struct_context: &'ctx StructContext,
}

impl<'ctx> Fold for ImportedMethodTransformer<'ctx> {
    fn fold_impl_item_fn(&mut self, node: ImplItemFn) -> ImplItemFn {
        let abi = get_abi(&node.sig);
        match (&node.vis, &abi.as_deref()) {
            (_, Some("java")) => {
                let constructor_attribute =
                    node.attrs.iter().find(|a| a.path().is_ident("constructor"));
                let is_constructor = {
                    match constructor_attribute {
                        Some(a) => {
                            if a.meta
                                .require_list()
                                .is_ok_and(|meta_list| !meta_list.tokens.is_empty())
                            {
                                emit_warning!(
                                    a.to_token_stream(),
                                    "#[constructor] attribute does not take parameters"
                                )
                            }
                            true
                        }
                        None => false,
                    }
                };

                if !node.block.stmts.is_empty() {
                    emit_error!(
                        node.block,
                        "`extern \"java\"` methods must have an empty body"
                    )
                }

                let mut original_signature = node.sig.clone();
                let self_method = is_self_method(&node.sig);
                let (signature, env_arg) = get_env_arg(node.sig.clone());
                let (mut signature, class_ref_arg) = get_class_arg_if_any(signature.clone());

                let impl_item_attributes: Vec<_> = {
                    let discarded_known_attributes: HashSet<&str> = {
                        let mut h = HashSet::new();
                        h.insert("call_type");

                        if is_constructor {
                            h.insert("constructor");
                        }
                        h
                    };

                    node.clone()
                        .attrs
                        .into_iter()
                        .filter(|a| {
                            !discarded_known_attributes
                                .contains(&a.path().segments.to_token_stream().to_string().as_str())
                        })
                        .collect()
                };

                let dummy = ImplItemFn {
                    sig: Signature {
                        abi: None,
                        ..original_signature.clone()
                    },
                    block: parse_quote! {{
                        unimplemented!()
                    }},
                    attrs: impl_item_attributes.clone(),
                    ..node.clone()
                };

                if is_constructor && self_method {
                    emit_error!(
                        original_signature,
                        "cannot have self methods declared as constructors"
                    );

                    return dummy;
                }

                if env_arg.is_none() {
                    if !self_method {
                        emit_error!(
                            original_signature,
                            "imported static methods must have a parameter of type `&JNIEnv` as first parameter"
                        );
                    } else {
                        emit_error!(
                            original_signature,
                            "imported self methods must have a parameter of type `&JNIEnv` as second parameter"
                        );
                    }
                    return dummy;
                }

                let call_type_attribute = get_call_type(&node);
                let call_type = call_type_attribute
                    .as_ref()
                    .map(|c| &c.call_type)
                    .unwrap_or(&CallType::Safe(None));

                if let Some(CallTypeAttribute { attr, .. }) = &call_type_attribute {
                    if let CallType::Safe(Some(params)) = call_type {
                        if let SafeParams {
                            message: Some(_), ..
                        }
                        | SafeParams {
                            exception_class: Some(_),
                            ..
                        } = params
                        {
                            abort!(attr, "can't have exception message or exception class for imported methods")
                        }
                    }
                }

                let jni_package_path = self
                    .struct_context
                    .package
                    .as_ref()
                    .map(|p| p.to_string())
                    .filter(|s| !s.is_empty())
                    .unwrap_or_else(|| "".into())
                    .replace('.', "/");

                let java_class_path = [jni_package_path, self.struct_context.struct_name.clone()]
                    .iter()
                    .filter(|s| !s.is_empty())
                    .map(|s| s.to_owned())
                    .collect::<Vec<_>>()
                    .join("/");
                let java_method_name = to_camel_case(&signature.ident.to_string());

                let input_types_conversions = signature
                    .inputs
                    .iter_mut()
                    .rev()
                    .filter_map(|i| match i {
                        FnArg::Typed(t) => match &*t.pat {
                            Pat::Ident(PatIdent { ident, .. }) if ident == "self" => None,
                            _ => Some((&t.ty, t.ty.span(), &mut t.attrs))
                        },
                        FnArg::Receiver(_) => None,
                    })
                    .map(|(t, span, attrs)| {
                        let override_input_type = attrs.iter().find(|attr| {
                            attr.path().segments.iter().find(|seg| seg.ident.to_string().as_str() == "input_type").is_some()
                        }).and_then(|a| {
                            if let Ok(meta_list) = a.meta.require_list() {
                                let token_tree_lit: Lit = syn::parse2::<Lit>(meta_list.clone().tokens).unwrap();

                                if let Lit::Str(literal) = token_tree_lit {
                                    Some(literal)
                                } else {
                                    None
                                }
                            } else {
                                abort!(a, "Missing argument for `#[input_type]`")
                            }
                        });

                        if let Some(override_input_type) = override_input_type {
                            quote_spanned! { span => #override_input_type, }
                        } else {
                            if let CallType::Safe(_) = call_type {
                                quote_spanned! { span => <#t as ::robusta_jni::convert::TryIntoJavaValue>::SIG_TYPE, }
                            } else {
                                quote_spanned! { span => <#t as ::robusta_jni::convert::IntoJavaValue>::SIG_TYPE, }
                            }
                        }
                    })
                    .fold(TokenStream::new(), |t, mut tok| {
                        t.to_tokens(&mut tok);
                        tok
                    });

                let output_type_span = {
                    match &signature.output {
                        ReturnType::Default => signature.output.span(),
                        ReturnType::Type(_arrow, ref ty) => ty.span(),
                    }
                };

                let output_conversion = match signature.output {
                    ReturnType::Default => quote_spanned!(signature.output.span() => ),
                    ReturnType::Type(_arrow, ref ty) => {
                        if is_constructor {
                            quote_spanned! { output_type_span => "V" }
                        } else {
                            match call_type {
                                CallType::Safe(_) => {
                                    let inner_result_ty = match &**ty {
                                        Type::Path(TypePath { path, .. }) => {
                                            path.segments.last().map(|s| match &s.arguments {
                                                PathArguments::AngleBracketed(a) => {
                                                    match &a.args.first().expect("return type must be `::robusta_jni::jni::errors::Result` when using \"java\" ABI with an implicit or \"safe\" `call_type`") {
                                                        GenericArgument::Type(t) => t,
                                                        _ => abort!(a, "first generic argument in return type must be a type")
                                                    }
                                                }
                                                PathArguments::None => {
                                                    let user_attribute_message = call_type_attribute.as_ref().map(|_| "because of this attribute");
                                                    abort!(s, "return type must be `::robusta_jni::jni::errors::Result` when using \"java\" ABI with an implicit or \"safe\" `call_type`";
                                                                        help = "replace `{}` with `Result<{}>`", s.ident, s.ident;
                                                                        help =? call_type_attribute.as_ref().map(|c| c.attr.span()).unwrap() => user_attribute_message)
                                                }
                                                _ => abort!(s, "return type must be `::robusta_jni::jni::errors::Result` when using \"java\" ABI with an implicit or \"safe\" `call_type`")
                                            })
                                        }
                                        _ => abort!(ty, "return type must be `::robusta_jni::jni::errors::Result` when using \"java\" ABI with an implicit or \"safe\" `call_type`")
                                    }.unwrap();

                                    quote_spanned! { output_type_span => <#inner_result_ty as ::robusta_jni::convert::TryIntoJavaValue>::SIG_TYPE }
                                }
                                CallType::Unchecked(_) => {
                                    if let Type::Path(TypePath { path, .. }) = ty.as_ref() {
                                        if let Some(r) =
                                            path.segments.last().filter(|i| i.ident == "Result")
                                        {
                                            if let PathArguments::AngleBracketed(_) = r.arguments {
                                                let call_type_span = call_type_attribute
                                                    .as_ref()
                                                    .map(|c| c.attr.span());
                                                let call_type_hint = call_type_span.map(|_| {
                                                    "maybe you meant `#[call_type(safe)]`?"
                                                });

                                                emit_warning!(ty, "using a `Result` type in a `#[call_type(unchecked)]` method";
                                            hint =? call_type_span.unwrap() => call_type_hint)
                                            }
                                        }
                                    }
                                    quote_spanned! { output_type_span => <#ty as ::robusta_jni::convert::IntoJavaValue>::SIG_TYPE }
                                }
                            }
                        }
                    }
                };

                let java_signature = quote_spanned! { signature.span() => ["(", #input_types_conversions ")", #output_conversion].join("") };

                let input_conversions = signature.inputs.iter().fold(TokenStream::new(), |mut tok, input| {
                    match input {
                        FnArg::Receiver(_) => { tok }
                        FnArg::Typed(t) => {
                            let ty = &t.ty;
                            let pat: TokenStream = {
                                // The things we do for ~love~ nice compiler errors...
                                // TODO: Check whether there is a better way to force spans onto token streams
                                let pat = &t.pat;
                                let mut p: TokenTree = parse_quote! { #pat };
                                p.set_span(ty.span());
                                p.into()
                            };

                            let conversion: TokenStream = if let CallType::Safe(_) = call_type {
                                quote_spanned! { ty.span() => ::std::convert::Into::into(<#ty as ::robusta_jni::convert::TryIntoJavaValue>::try_into(#pat, &env)?), }
                            } else {
                                quote_spanned! { ty.span() => ::std::convert::Into::into(<#ty as ::robusta_jni::convert::IntoJavaValue>::into(#pat, &env)), }
                            };
                            conversion.to_tokens(&mut tok);
                            tok
                        }
                    }
                });

                let return_expr = match call_type {
                    CallType::Safe(_) => {
                        if is_constructor {
                            quote_spanned! { output_type_span =>
                                res.and_then(|v| ::robusta_jni::convert::TryFromJavaValue::try_from(v, &env))
                            }
                        } else {
                            quote_spanned! { output_type_span =>
                                res.and_then(|v| ::std::convert::TryInto::try_into(::robusta_jni::convert::JValueWrapper::from(v)))
                                   .and_then(|v| ::robusta_jni::convert::TryFromJavaValue::try_from(v, &env))
                            }
                        }
                    }
                    CallType::Unchecked(_) => {
                        if is_constructor {
                            quote_spanned! { output_type_span =>
                                ::robusta_jni::convert::FromJavaValue::from(res, &env)
                            }
                        } else {
                            quote_spanned! { output_type_span =>
                                ::std::convert::TryInto::try_into(::robusta_jni::convert::JValueWrapper::from(res))
                                    .map(|v| ::robusta_jni::convert::FromJavaValue::from(v, &env))
                                    .unwrap()
                            }
                        }
                    }
                };

                let env_ident = match env_arg.unwrap() {
                    FnArg::Typed(t) => {
                        match *t.pat {
                            Pat::Ident(PatIdent { ident, .. }) => ident,
                            _ => panic!("non-ident pat in FnArg")
                        }
                    }
                    _ => panic!("Bug -- please report to library author. Expected env parameter, found receiver")
                };

                let sig_discarded_known_attributes: HashSet<&str> = {
                    let mut h = HashSet::new();
                    h.insert("input_type");

                    h
                };

                let class_arg_ident = if let Some(class_ref_arg) = class_ref_arg {
                    match class_ref_arg {
                        FnArg::Typed(t) => {
                            match *t.pat {
                                Pat::Ident(PatIdent { ident, .. }) => Some(ident),
                                _ => panic!("non-ident pat in FnArg")
                            }
                        },
                        _ => panic!("Bug -- please report to library author. Expected env parameter, found receiver")
                    }
                } else {
                    None
                };

                original_signature.inputs.iter_mut().for_each(|i| match i {
                    FnArg::Typed(t) => match &*t.pat {
                        Pat::Ident(PatIdent { ident, .. }) if ident == "self" => {}
                        _ => {
                            t.attrs = t
                                .attrs
                                .clone()
                                .into_iter()
                                .filter(|a| {
                                    !a.path()
                                        .segments
                                        .iter()
                                        .find(|s| {
                                            sig_discarded_known_attributes
                                                .iter()
                                                .any(|d| s.ident.to_string().contains(d))
                                        })
                                        .is_some()
                                })
                                .collect()
                        }
                    },
                    FnArg::Receiver(_) => {}
                });

                ImplItemFn {
                    sig: Signature {
                        abi: None,
                        ..original_signature
                    },
                    block: if self_method {
                        let self_span = node.sig.inputs.iter().next().unwrap().span();
                        match call_type {
                            CallType::Safe(_) => {
                                parse_quote_spanned! { self_span => {
                                    let env: &'_ ::robusta_jni::jni::JNIEnv<'_> = #env_ident;
                                    let res = env.call_method(::robusta_jni::convert::JavaValue::autobox(::robusta_jni::convert::TryIntoJavaValue::try_into(self, &env)?, &env), #java_method_name, #java_signature, &[#input_conversions]);
                                    #return_expr
                                }}
                            }
                            CallType::Unchecked(_) => {
                                parse_quote_spanned! { self_span => {
                                    let env: &'_ ::robusta_jni::jni::JNIEnv<'_> = #env_ident;
                                    let res = env.call_method(::robusta_jni::convert::JavaValue::autobox(::robusta_jni::convert::IntoJavaValue::into(self, &env), &env), #java_method_name, #java_signature, &[#input_conversions]).unwrap();
                                    #return_expr
                                }}
                            }
                        }
                    } else {
                        match call_type {
                            CallType::Safe(_) => {
                                if is_constructor {
                                    if let Some(class_arg_ident) = class_arg_ident {
                                        parse_quote! {{
                                            let env: &'_ ::robusta_jni::jni::JNIEnv<'_> = #env_ident;
                                            let res = env.new_object(#class_arg_ident, #java_signature, &[#input_conversions]);
                                            #return_expr
                                        }}
                                    } else {
                                        parse_quote! {{
                                            let env: &'_ ::robusta_jni::jni::JNIEnv<'_> = #env_ident;
                                            let res = env.new_object(#java_class_path, #java_signature, &[#input_conversions]);
                                            #return_expr
                                        }}
                                    }
                                } else {
                                    if let Some(class_arg_ident) = class_arg_ident {
                                        parse_quote! {{
                                            let env: &'_ ::robusta_jni::jni::JNIEnv<'_> = #env_ident;
                                            let res = env.call_static_method(#class_arg_ident, #java_method_name, #java_signature, &[#input_conversions]);
                                            #return_expr
                                        }}
                                    } else {
                                        parse_quote! {{
                                            let env: &'_ ::robusta_jni::jni::JNIEnv<'_> = #env_ident;
                                            let res = env.call_static_method(#java_class_path, #java_method_name, #java_signature, &[#input_conversions]);
                                            #return_expr
                                        }}
                                    }
                                }
                            }
                            CallType::Unchecked(_) => {
                                if is_constructor {
                                    if let Some(class_arg_ident) = class_arg_ident {
                                        parse_quote! {{
                                            let env: &'_ ::robusta_jni::jni::JNIEnv<'_> = #env_ident;
                                            let res = env.new_object(#class_arg_ident, #java_signature, &[#input_conversions]).unwrap();
                                            #return_expr
                                        }}
                                    } else {
                                        parse_quote! {{
                                            let env: &'_ ::robusta_jni::jni::JNIEnv<'_> = #env_ident;
                                            let res = env.new_object(#java_class_path, #java_signature, &[#input_conversions]).unwrap();
                                            #return_expr
                                        }}
                                    }
                                } else {
                                    if let Some(class_arg_ident) = class_arg_ident {
                                        parse_quote! {{
                                            let env: &'_ ::robusta_jni::jni::JNIEnv<'_> = #env_ident;
                                            let res = env.call_static_method(#class_arg_ident, #java_method_name, #java_signature, &[#input_conversions]).unwrap();
                                            #return_expr
                                        }}
                                    } else {
                                        parse_quote! {{
                                            let env: &'_ ::robusta_jni::jni::JNIEnv<'_> = #env_ident;
                                            let res = env.call_static_method(#java_class_path, #java_method_name, #java_signature, &[#input_conversions]).unwrap();
                                            #return_expr
                                        }}
                                    }
                                }
                            }
                        }
                    },
                    attrs: impl_item_attributes,
                    ..node
                }
            }

            _ => node,
        }
    }
}



================================================
FILE: robusta-codegen/src/transformation/mod.rs
================================================
use std::collections::{BTreeSet, HashSet};
use std::fmt::{Display, Formatter};
use std::str::FromStr;

use darling::util::Flag;
use darling::FromMeta;
use proc_macro2::{Ident, TokenStream};
use proc_macro_error::{emit_error, emit_warning};
use quote::ToTokens;
use syn::fold::Fold;
use syn::parse::{Parse, ParseBuffer, ParseStream, Parser};
use syn::punctuated::Punctuated;
use syn::spanned::Spanned;
use syn::visit::Visit;
use syn::{
    parse_quote, Attribute, FnArg, GenericArgument, GenericParam, ImplItemFn, Item, ItemImpl,
    ItemMod, ItemStruct, Lit, Pat, PatIdent, PatType, Path, PathArguments, PathSegment, Type,
    TypePath, TypeReference, Visibility,
};
use syn::{Error, ImplItem, Token};

use imported::ImportedMethodTransformer;

use crate::transformation::context::StructContext;
use crate::transformation::exported::ExportedMethodTransformer;
use crate::utils::{canonicalize_path, get_abi};
use crate::validation::JNIBridgeModule;
use std::fmt;

#[macro_use]
mod utils;
mod context;
mod exported;
mod imported;

#[derive(Copy, Clone)]
pub(crate) enum ImplItemType {
    Exported,
    Imported,
    Unexported,
}

pub(crate) struct ModTransformer {
    module: JNIBridgeModule,
}

impl ModTransformer {
    pub(crate) fn new(module: JNIBridgeModule) -> Self {
        ModTransformer { module }
    }

    pub(crate) fn transform_module(&mut self) -> TokenStream {
        let module_decl = self.module.module_decl.clone();
        self.fold_item_mod(module_decl).into_token_stream()
    }

    /// If the impl block is a standard impl block for a type, makes every exported fn a freestanding one
    fn transform_item_impl(&mut self, node: ItemImpl) -> TokenStream {
        let mut impl_export_visitor = ImplExportVisitor::default();
        impl_export_visitor.visit_item_impl(&node);

        let (preserved_items, transformed_items) = if let Type::Path(p) = &*node.self_ty {
            let canonical_path = canonicalize_path(&p.path);
            let struct_name = canonical_path
                .to_token_stream()
                .to_string()
                .replace(" ", ""); // TODO: Replace String-based struct name matching with something more robust
            let struct_package = self.module.package_map.get(&struct_name).cloned().flatten();

            if struct_package.is_none() {
                emit_error!(p.path, "can't find package for struct `{}`", struct_name);
                return node.to_token_stream();
            }

            let path_lifetimes: BTreeSet<String> = p
                .path
                .segments
                .iter()
                .filter_map(|s: &PathSegment| {
                    if let PathArguments::AngleBracketed(a) = &s.arguments {
                        Some(a.args.iter().filter_map(|g| match g {
                            GenericArgument::Lifetime(l) => Some(l.ident.to_string()),
                            _ => None,
                        }))
                    } else {
                        None
                    }
                })
                .flatten()
                .collect();

            let struct_lifetimes: Vec<_> = node
                .generics
                .params
                .iter()
                .filter_map(|p| match p {
                    GenericParam::Lifetime(l)
                        if path_lifetimes.contains(&l.lifetime.ident.to_string()) =>
                    {
                        Some(l.clone())
                    }
                    _ => None,
                })
                .collect();

            let context = StructContext {
                struct_type: p.path.clone(),
                struct_name,
                struct_lifetimes,
                package: struct_package,
            };

            let mut exported_fns_transformer = ExportedMethodTransformer {
                struct_context: &context,
            };
            let mut imported_fns_transformer = ImportedMethodTransformer {
                struct_context: &context,
            };
            let mut impl_cleaner = ImplCleaner;

            let preserved = impl_export_visitor
                .items
                .iter()
                .map(|(i, t)| {
                    let item = (*i).clone();
                    match t {
                        ImplItemType::Exported => impl_cleaner.fold_impl_item(item),
                        ImplItemType::Imported => imported_fns_transformer
                            .fold_impl_item(impl_cleaner.fold_impl_item(item)),
                        ImplItemType::Unexported => item,
                    }
                })
                .collect();

            let transformed = impl_export_visitor
                .items
                .into_iter()
                .filter_map(|(i, t)| match t {
                    ImplItemType::Exported => Some(i),
                    _ => None,
                })
                .cloned()
                .map(|i| exported_fns_transformer.fold_impl_item(i))
                .collect();

            (preserved, transformed)
        } else {
            (node.items, Vec::new())
        };

        let preserved_impl = ItemImpl {
            attrs: node
                .attrs
                .into_iter()
                .map(|a| self.fold_attribute(a))
                .collect(),
            generics: self.fold_generics(node.generics),
            self_ty: Box::new(self.fold_type(*node.self_ty)),
            items: preserved_items
                .into_iter()
                .map(|i| self.fold_impl_item(i))
                .collect(),
            ..node
        };

        transformed_items.iter().map(|i| i.to_token_stream()).fold(
            preserved_impl.into_token_stream(),
            |item, mut stream| {
                item.to_tokens(&mut stream);
                stream
            },
        )
    }
}

impl Fold for ModTransformer {
    fn fold_item(&mut self, node: Item) -> Item {
        match node {
            Item::Const(c) => Item::Const(self.fold_item_const(c)),
            Item::Enum(e) => Item::Enum(self.fold_item_enum(e)),
            Item::ExternCrate(c) => Item::ExternCrate(self.fold_item_extern_crate(c)),
            Item::Fn(f) => Item::Fn(self.fold_item_fn(f)),
            Item::ForeignMod(m) => Item::ForeignMod(self.fold_item_foreign_mod(m)),
            Item::Impl(i) => Item::Verbatim(self.transform_item_impl(i)),
            Item::Macro(m) => Item::Macro(self.fold_item_macro(m)),
            Item::Mod(m) => Item::Mod(self.fold_item_mod(m)),
            Item::Static(s) => Item::Static(self.fold_item_static(s)),
            Item::Struct(s) => Item::Struct(self.fold_item_struct(s)),
            Item::Trait(t) => Item::Trait(self.fold_item_trait(t)),
            Item::TraitAlias(t) => Item::TraitAlias(self.fold_item_trait_alias(t)),
            Item::Type(t) => Item::Type(self.fold_item_type(t)),
            Item::Union(u) => Item::Union(self.fold_item_union(u)),
            Item::Use(u) => Item::Use(self.fold_item_use(u)),
            Item::Verbatim(_) => node,
            _ => node,
        }
    }

    fn fold_item_mod(&mut self, mut node: ItemMod) -> ItemMod {
        let allow_non_snake_case: Attribute = parse_quote! { #![allow(non_snake_case)] };

        node.attrs.extend_from_slice(&[allow_non_snake_case]);

        ItemMod {
            attrs: node.attrs,
            vis: self.fold_visibility(node.vis),
            unsafety: node.unsafety,
            mod_token: node.mod_token,
            ident: self.fold_ident(node.ident),
            content: node.content.map(|(brace, items)| {
                (
                    brace,
                    items.into_iter().map(|i| self.fold_item(i)).collect(),
                )
            }),
            semi: node.semi,
        }
    }

    fn fold_item_struct(&mut self, node: ItemStruct) -> ItemStruct {
        let struct_attributes = {
            /* The `#[bridge]` attribute macro has to discard `#[package()]` attributes, because they don't exists in standard Rust
             * and currently there is no way for attribute macros to automatically introduce inert attributes (see: https://doc.rust-lang.org/reference/attributes.html#active-and-inert-attributes
             * and rust-lang/issues/#65823).
             * However, we want `#[package()]` to also be used in combination with auto-derive, and conversion traits (i.e. `Signature`, `(Try)IntoJavaValue`, `(Try)FromJavaValue`) *need* a `#[package]` attribute on the struct they are applied on.
             * If we remove the package attribute blindly the traits cannot see it, and if we keep it the auto-derived traits cannot remove it (auto-derive macros cannot modify the existing token stream as proc macros).
             * Here we check wether the struct has a `#[derive(TRAIT)]` (crudely with a string comparison and hoping the user never writes `#[derive(::robusta_jni::convert::TRAIT)]`)
             * if it is present we don't remove `#[package]`, otherwise we remove it.
             * This works because all conversion traits auto-derive macros also declare `#[package]` as a helper attribute
             */
            let attributes = node.attrs.clone();
            let traits_with_package_attr = HashSet::from([
                "Signature",
                "FromJavaValue",
                "TryFromJavaValue",
                "IntoJavaValue",
                "TryIntoJavaValue",
            ]);

            let has_package_trait = node.attrs.iter().any(|a| {
                let is_derive =
                    a.path().get_ident().map(ToString::to_string).as_deref() == Some("derive");
                let derived_traits = a
                    .parse_args_with(Punctuated::<Ident, Token![,]>::parse_terminated)
                    .iter()
                    .flat_map(|p: &syn::punctuated::Punctuated<Ident, Token![,]>| p)
                    .map(|i| i.to_string())
                    .collect::<HashSet<String>>();
                let needs_package_attr = derived_traits
                    .iter()
                    .any(|t| traits_with_package_attr.contains(t.as_str()));

                is_derive && needs_package_attr
            });

            if !has_package_trait {
                attributes
                    .into_iter()
                    .filter(|a| a.path().to_token_stream().to_string().as_str() != "package")
                    .collect()
            } else {
                attributes
            }
        };

        ItemStruct {
            attrs: struct_attributes,
            vis: node.vis,
            struct_token: node.struct_token,
            ident: node.ident,
            generics: self.fold_generics(node.generics),
            fields: self.fold_fields(node.fields),
            semi_token: node.semi_token,
        }
    }
}

#[derive(Default)]
pub struct ImplExportVisitor<'ast> {
    pub(crate) items: Vec<(&'ast ImplItem, ImplItemType)>,
}

impl<'ast> Visit<'ast> for ImplExportVisitor<'ast> {
    fn visit_impl_item(&mut self, node: &'ast ImplItem) {
        match node {
            ImplItem::Fn(method) => {
                let abi = get_abi(&method.sig);

                match abi.as_deref() {
                    Some("jni") => self.items.push((node, ImplItemType::Exported)),
                    Some("java") => self.items.push((node, ImplItemType::Imported)),
                    _ => self.items.push((node, ImplItemType::Unexported)),
                }
            }
            _ => self.items.push((node, ImplItemType::Unexported)),
        }
    }
}

#[derive(Clone, Ord, PartialOrd, Eq, PartialEq)]
pub(crate) struct JavaPath(String);

impl Display for JavaPath {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.0)
    }
}

impl FromStr for JavaPath {
    type Err = String;

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        let input = s.to_string().replace(' ', "");
        if input.contains('-') {
            Err("package names can't contain dashes".into())
        } else {
            Ok(JavaPath(input))
        }
    }
}

impl JavaPath {
    pub fn to_snake_case(&self) -> String {
        self.0.replace('.', "_")
    }

    pub fn to_classpath_path(&self) -> String {
        self.0.replace('.', "/")
    }
}

impl Parse for JavaPath {
    fn parse<'a>(input: &'a ParseBuffer<'a>) -> syn::Result<Self> {
        let tokens = Punctuated::<Ident, Token![.]>::parse_terminated(input)?.to_token_stream();
        let package = tokens.to_string();

        JavaPath::from_str(&package).map_err(|e| Error::new_spanned(tokens, e))
    }
}

impl FromMeta for JavaPath {
    fn from_value(value: &Lit) -> darling::Result<Self> {
        use darling::Error;

        if let Lit::Str(literal) = value {
            let path = literal.value();
            Self::from_string(&path)
        } else {
            Err(Error::custom("invalid type"))
        }
    }

    fn from_string(path: &str) -> darling::Result<Self> {
        use darling::Error;
        if path.contains('-') {
            Err(Error::custom(
                "invalid path: packages and classes cannot contain dashes",
            ))
        } else {
            let tokens = TokenStream::from_str(&path)
                .map_err(|_| Error::custom("cannot create token stream for java path parsing"))?;
            let _parsed: Punctuated<Ident, Token![.]> =
                Punctuated::<Ident, Token![.]>::parse_separated_nonempty
                    .parse(tokens.into())
                    .map_err(|e| Error::custom(format!("cannot parse java path ({})", e)))?;

            Ok(JavaPath(path.into()))
        }
    }
}

pub(crate) struct AttributeFilter<'ast> {
    pub whitelist: HashSet<Path>,
    pub filtered_attributes: Vec<&'ast Attribute>,
}

impl<'ast> AttributeFilter<'ast> {
    pub(crate) fn with_whitelist(whitelist: HashSet<Path>) -> Self {
        AttributeFilter {
            whitelist,
            filtered_attributes: Vec::new(),
        }
    }
}

impl<'ast> Visit<'ast> for AttributeFilter<'ast> {
    fn visit_attribute(&mut self, attribute: &'ast Attribute) {
        if self.whitelist.contains(&attribute.path()) {
            self.filtered_attributes.push(attribute);
        }
    }
}

struct ImplCleaner;

impl Fold for ImplCleaner {
    fn fold_impl_item_fn(&mut self, mut node: ImplItemFn) -> ImplItemFn {
        let abi = node
            .sig
            .abi
            .as_ref()
            .and_then(|l| l.name.as_ref().map(|n| n.value()));

        match (&node.vis, &abi.as_deref()) {
            (Visibility::Public(_), Some("jni")) => {
                node.sig.abi = None;
                node.attrs = node
                    .attrs
                    .into_iter()
                    .filter(|a| a.path().get_ident().map_or(false, |i| i != "call_type"))
                    .collect();

                node
            }
            (_, _) => node,
        }
    }
}

struct FreestandingTransformer {
    struct_type: Path,
}

impl FreestandingTransformer {
    fn new(struct_type: Path) -> Self {
        FreestandingTransformer { struct_type }
    }
}

impl Fold for FreestandingTransformer {
    fn fold_fn_arg(&mut self, arg: FnArg) -> FnArg {
        match arg {
            FnArg::Receiver(r) => {
                let receiver_span = r.span();

                let needs_env_lifetime = self.struct_type.segments.iter().any(|s| {
                    if let PathArguments::AngleBracketed(a) = &s.arguments {
                        a.args
                            .iter()
                            .filter_map(|g| match g {
                                GenericArgument::Lifetime(l) => Some(l),
                                _ => None,
                            })
                            .all(|l| l.ident != "env")
                    } else {
                        false
                    }
                });

                if needs_env_lifetime {
                    emit_warning!(self.struct_type, "must have one `'env` lifetime in impl to support self methods when using lifetime-parametrized struct");
                }

                let self_type = match r.reference.clone() {
                    Some((and_token, lifetime)) => Type::Reference(TypeReference {
                        and_token,
                        lifetime,
                        mutability: r.mutability,
                        elem: Box::new(Type::Path(TypePath {
                            qself: None,
                            path: self.struct_type.clone(),
                        })),
                    }),

                    None => Type::Path(TypePath {
                        qself: None,
                        path: self.struct_type.clone(),
                    }),
                };

                FnArg::Typed(PatType {
                    attrs: r.attrs,
                    pat: Box::new(Pat::Ident(PatIdent {
                        attrs: vec![],
                        by_ref: None,
                        mutability: None,
                        ident: Ident::new("receiver", receiver_span),
                        subpat: None,
                    })),
                    colon_token: Token![:](receiver_span),
                    ty: Box::new(parse_quote! { #self_type }),
                })
            }

            FnArg::Typed(t) => match &*t.pat {
                Pat::Ident(ident) if ident.ident == "self" => {
                    let pat_span = t.span();
                    let self_type = &*t.ty;
                    FnArg::Typed(PatType {
                        attrs: t.attrs,
                        pat: Box::new(Pat::Ident(PatIdent {
                            attrs: ident.attrs.clone(),
                            by_ref: ident.by_ref,
                            mutability: ident.mutability,
                            ident: Ident::new("receiver", pat_span),
                            subpat: ident.subpat.clone(),
                        })),
                        colon_token: t.colon_token,
                        ty: Box::new(parse_quote! { #self_type }),
                    })
                }
                _ => FnArg::Typed(t),
            },
        }
    }
}

#[derive(Clone, Default, FromMeta)]
#[darling(default)]
pub struct SafeParams {
    pub(crate) exception_class: Option<JavaPath>,
    pub(crate) message: Option<String>,
}

#[derive(Clone, FromMeta)]
pub enum CallType {
    Safe(Option<SafeParams>),
    Unchecked(Flag),
}

pub struct CallTypeAttribute {
    pub(crate) attr: Attribute,
    pub(crate) call_type: CallType,
}

impl Parse for CallTypeAttribute {
    fn parse(input: ParseStream<'_>) -> syn::Result<Self> {
        let attribute = input
            .call(Attribute::parse_outer)?
            .first()
            .cloned()
            .ok_or_else(|| Error::new(input.span(), "Invalid parsing of `call_type` attribute "))?;

        if attribute.path().get_ident().ok_or_else(|| {
            Error::new(attribute.path().span(), "expected identifier for attribute")
        })? != "call_type"
        {
            return Err(Error::new(
                attribute.path().span(),
                "expected identifier `call_type` for attribute",
            ));
        }

        let attr_meta = attribute.meta.clone();

        // Special-case `call_type(safe)` without further parentheses
        // TODO: Find out if it's possible to use darling to allow `call_type(safe)` *and* `call_type(safe(message = "foo"))` etc.
        if attr_meta.to_token_stream().to_string() == "call_type(safe)" {
            Ok(CallTypeAttribute {
                attr: attribute,
                call_type: CallType::Safe(None),
            })
        } else {
            CallType::from_meta(&attr_meta)
                .map_err(|e| {
                    Error::new(
                        attr_meta.span(),
                        format!("invalid `call_type` attribute options ({})", e),
                    )
                })
                .map(|c| CallTypeAttribute {
                    attr: attribute,
                    call_type: c,
                })
        }
    }
}



================================================
FILE: robusta-codegen/src/transformation/utils.rs
================================================
use std::collections::HashSet;
use std::str::FromStr;

use proc_macro2::TokenStream;
use proc_macro_error::emit_warning;
use quote::ToTokens;
use syn::visit::Visit;
use syn::ImplItemFn;

use crate::transformation::{AttributeFilter, CallTypeAttribute};

pub(crate) fn get_call_type(node: &ImplItemFn) -> Option<CallTypeAttribute> {
    let whitelist = {
        let mut f = HashSet::new();
        f.insert(syn::parse2(TokenStream::from_str("call_type").unwrap()).unwrap());
        f
    };

    let mut attributes_collector = AttributeFilter::with_whitelist(whitelist);
    attributes_collector.visit_impl_item_fn(&node);

    let call_type_attribute = attributes_collector.filtered_attributes.first().and_then(|call_type_attr| {
        syn::parse2(call_type_attr.to_token_stream()).map_err(|e| {
            emit_warning!(e.span(), format!("invalid parsing of `call_type` attribute, defaulting to #[call_type(safe)]. {}", e));
            e
        }).ok()
    });

    call_type_attribute
}

macro_rules! parse_quote_spanned {
    ($span:expr => $($tt:tt)*) => {
        syn::parse2(quote::quote_spanned!($span => $($tt)*)).unwrap_or_else(|e| panic!("{}", e))
    };
}



================================================
FILE: robusta-example/Cargo.toml
================================================
[package]
name = "robusta-example"
version = "0.2.2"
authors = ["Giovanni Berti <dev.giovanniberti@gmail.com>"]
edition = "2018"

[lib]
crate-type = ["cdylib"]

[dependencies]
robusta_jni = { path = "../.", version = "0.2" }



================================================
FILE: robusta-example/Makefile
================================================
java_run: lib
	javac com/example/robusta/HelloWorld.java && RUST_BACKTRACE=full java -Djava.library.path=../target/debug com.example.robusta.HelloWorld

.PHONY: lib

lib:
	cargo build



================================================
FILE: robusta-example/com/example/robusta/HelloWorld.java
================================================
package com.example.robusta;

import java.util.*;

class HelloWorld {
    private String foo = "";

    private static native ArrayList<String> special(ArrayList<Integer> input1, int in2);

    // pub extern "java" fn staticJavaAdd(i: i32, u: i32) -> i32 {}
    public static int staticJavaAdd(int i, int u) {
        return i + u;
    }

    // pub extern "jni" fn catchMe(self, _env: &JNIEnv) -> JniResult<i32>
    private native void catchMe() throws IllegalArgumentException;

    // pub extern "java" fn javaAdd(&self, i: i32, u: i32) -> i32 {}
    public int javaAdd(int i, int u) {
        return i + u;
    }

    public String javaAdd(String i, int f, String u) {
            return i + u;
    }

    // pub extern "jni" fn nativeFun(self, static_call: bool) -> i32
    public native int nativeFun(boolean staticCall);

    static {
        System.loadLibrary("robusta_example");
    }

    private native void setStringHelloWorld();

    public static void main(String[] args) {
        ArrayList<String> output = HelloWorld.special(new ArrayList<Integer>(List.of(1, 2, 3)), 4);
        System.out.println(output);

        HelloWorld h = new HelloWorld();
        System.out.println(h.nativeFun(false));
        System.out.println(h.nativeFun(true));

        try {
            h.catchMe();
        } catch (IllegalArgumentException e) {
            System.out.println("Caught exception. Message: \"" + e.getMessage() + "\"");
            System.out.println("Printing stacktrace:");
            e.printStackTrace();
        }

        System.out.println("Now h.foo is: \"" + h.foo + "\"");
        h.setStringHelloWorld();
        System.out.println("After setStringHelloWorld() h.foo is: \"" + h.foo + "\"");
	}
}



================================================
FILE: robusta-example/src/lib.rs
================================================
use robusta_jni::bridge;

#[bridge]
mod jni {
    use robusta_jni::convert::{
        Field, IntoJavaValue, Signature, TryFromJavaValue, TryIntoJavaValue,
    };
    use robusta_jni::jni::errors::Error as JniError;
    use robusta_jni::jni::errors::Result as JniResult;
    use robusta_jni::jni::objects::AutoLocal;
    use robusta_jni::jni::JNIEnv;

    #[derive(Signature, TryIntoJavaValue, IntoJavaValue, TryFromJavaValue)]
    #[package(com.example.robusta)]
    pub struct HelloWorld<'env: 'borrow, 'borrow> {
        #[instance]
        raw: AutoLocal<'env, 'borrow>,
        #[field]
        foo: Field<'env, 'borrow, String>,
    }

    impl<'env: 'borrow, 'borrow> HelloWorld<'env, 'borrow> {
        #[constructor]
        pub extern "java" fn new(env: &'borrow JNIEnv<'env>) -> JniResult<Self> {}

        pub extern "jni" fn special(mut input1: Vec<i32>, input2: i32) -> Vec<String> {
            input1.push(input2);
            input1.iter().map(ToString::to_string).collect()
        }

        pub extern "jni" fn nativeFun(self, env: &JNIEnv, static_call: bool) -> JniResult<i32> {
            if static_call {
                Ok(HelloWorld::staticJavaAdd(env, 1, 2))
            } else {
                let a = self.javaAdd(env, 0, 0)?;
                Ok(a + self.javaAdd(env, 1, 2)?)
            }
        }

        #[call_type(safe(
            exception_class = "java.lang.IllegalArgumentException",
            message = "something bad happened"
        ))]
        pub extern "jni" fn catchMe(self, _env: &JNIEnv) -> JniResult<i32> {
            Err(JniError::NullPtr("catch me if you can"))
        }

        pub extern "java" fn javaAdd(&self, _env: &JNIEnv, i: i32, u: i32) -> JniResult<i32> {}

        #[call_type(unchecked)]
        pub extern "java" fn staticJavaAdd(env: &JNIEnv, i: i32, u: i32) -> i32 {}

        pub extern "jni" fn setStringHelloWorld(mut self) -> JniResult<()> {
            println!("[rust]: self.foo: \"{}\"", self.foo.get()?);
            self.foo.set("hello world".into())?;
            Ok(())
        }
    }
}



================================================
FILE: src/lib.rs
================================================
//! `robusta_jni` is a library that provides a procedural macro to make easier to write JNI-compatible code in Rust.
//!
//! It can perform automatic conversion of Rust-y input and output types.
//!
//! ```toml
//! [dependencies]
//! robusta_jni = "0.2"
//! ```
//!
//! # Getting started
//! The [`#[bridge]`](bridge) attribute is `robusta_jni`'s entry point. It must be applied to a module.
//! `robusta_jni` will then generate proper function definitions and trait implementations depending on declared methods.
//!
//! # Declaring classes
//! Rust counterparts of Java classes are declared as Rust `struct`s, with a `#[package(my.package.name)]` attribute.
//! When using the default package, just omit the package name inside parentheses.
//!
//! Structs without the package attribute will be ignored by `robusta_jni`.
//!
//! In order to use the features of `robusta_jni`, declared structs should also implement the [`Signature`] trait.
//! This can be done manually or with autoderive.
//!
//! Example:
//! ```rust
//! use robusta_jni::bridge;
//! use robusta_jni::convert::Signature;
//!
//! #[bridge]
//! mod jni {
//!     # use robusta_jni::convert::Signature;
//!     #[package()] // default package
//!     struct A;
//!
//!     impl Signature for A {
//!         const SIG_TYPE: &'static str = "LA;";
//!     }
//!
//!     #[derive(Signature)]
//!     #[package(my.awesome.package)]
//!     struct B;
//! }
//! ```
//!
//! # Adding native methods
//! JNI bindings are generated for every method implemented for `package`-annotated structs.
//! Each method can optionally specify a `#[call_type]` attribute that will determine how conversions between Rust and Java types are performed.
//! For more information about conversions and `#[call_type]`, check out the [convert](convert) module.
//!
//! In general, **all input and output types must implement proper conversion traits**
//! (input types must implement `(Try)FromJavaValue` and output types must implement `(Try)IntoJavaValue`)
//!
//! Native methods can optionally accept a [`JNIEnv`] parameter as first parameter (after `self` if present).
//!
//! Methods are declared as standard Rust functions with public visibility and "jni" ABI, and are matched by name with Java methods.
//! No special handling is needed.
//!
//! Example:
//!
//! ```rust
//! # use robusta_jni::bridge;
//! #
//! # #[bridge]
//! # mod jni {
//!     # use robusta_jni::convert::{Signature, TryFromJavaValue, JavaValue};
//!     # use robusta_jni::jni::JNIEnv;
//!     # use jni::objects::JObject;
//!     # #[derive(Signature)]
//!     # #[package()]
//!     # struct A;
//!     #
//!     # impl<'env: 'borrow, 'borrow> TryFromJavaValue<'env, 'borrow> for A {
//!     #    type Source = JObject<'env>;
//!     #
//!     #    fn try_from(s: Self::Source,env: &'borrow JNIEnv<'env>) -> ::robusta_jni::jni::errors::Result<Self> {
//!     #         Ok(A)
//!     #     }
//!     # }
//!     #
//! impl A {
//!     pub extern "jni" fn op(self, _env: &JNIEnv, flag: bool) -> i32 {
//!         //                       ^^^^^ optional
//!         if flag {
//!             1
//!         } else {
//!             0
//!         }
//!     }
//!
//!     // here the `env` parameter is omitted
//!     pub extern "jni" fn special(mut input1: Vec<i32>, input2: i32) -> Vec<String> {
//!         input1.push(input2);
//!         input1.iter().map(ToString::to_string).collect()
//!     }
//!
//! }
//! # }
//! ```
//!
//! # Adding Java methods
//! You can also declare Java methods and `robusta` will generate binding glue to convert types and call methods on the Java side.
//! Again, **all input and output types must implement proper conversion traits**: in this case it's the reverse from the Java to Rust case
//! (input types must implement `(Try)IntoJavaValue` and output types must implement `(Try)FromJavaValue`).
//!
//! Methods are declared as standard Rust functions with public visibility, a "java" ABI and an empty body, and are matched by name with Java methods.
//! Both static and non-static methods must accept a [`JNIEnv`] parameter as first parameter (after self if present).
//!
//! Constructors can be declared via a `#[constructor]` attribute on static methods, and are matched by their type signature.
//!
//! When using `#[call_type(safe)]` or omitting `call_type` attribute, the output type **must** be [`jni::errors::Result<T>`](jni::errors::Result)
//! with `T` being the actual method return type. Otherwise when using `#[call_type(unchecked)]` `T` is sufficient.
//!
//! **When using `#[call_type(unchecked)]` if a Java exception is thrown while calling a method a panic is raised.**
//!
//! ## Static methods
//!
//! Example:
//! ```rust
//! # use robusta_jni::bridge;
//! # use robusta_jni::convert::{Signature, TryFromJavaValue};
//! #
//! # #[bridge]
//! # mod jni {
//!     # use robusta_jni::convert::{Signature, TryFromJavaValue, JavaValue};
//!     # use robusta_jni::jni::JNIEnv;
//!     # use jni::objects::JObject;
//!     # #[derive(Signature)]
//!     # #[package()]
//!     # struct A;
//!     #
//!     # impl<'env: 'borrow, 'borrow> TryFromJavaValue<'env, 'borrow> for A {
//!     #    type Source = JObject<'env>;
//!     #
//!     #    fn try_from(s: Self::Source,env: &'borrow JNIEnv<'env>) -> ::robusta_jni::jni::errors::Result<Self> {
//!     #         Ok(A)
//!     #     }
//!     # }
//!     #
//! impl A {
//!     pub extern "java" fn staticJavaMethod(
//!         env: &JNIEnv,
//!         i: i32,
//!         u: i32,
//!     ) -> ::robusta_jni::jni::errors::Result<i32> {}
//! }
//! # }
//! ```
//!
//! ## Non-static methods
//!
//! Example:
//! ```rust
//! # use robusta_jni::bridge;
//! # use robusta_jni::convert::{Signature, TryFromJavaValue};
//! #
//! # #[bridge]
//! # mod jni {
//!     # use robusta_jni::convert::{Signature, TryFromJavaValue, JavaValue, TryIntoJavaValue};
//!     # use robusta_jni::jni::JNIEnv;
//!     # use jni::objects::JObject;
//!     # #[derive(Signature)]
//!     # #[package()]
//!     # struct A;
//!     #
//!     # impl<'env: 'borrow, 'borrow> TryFromJavaValue<'env, 'borrow> for A {
//!     #    type Source = JObject<'env>;
//!     #
//!     #    fn try_from(s: Self::Source,env: &'borrow JNIEnv<'env>) -> ::robusta_jni::jni::errors::Result<Self> {
//!     #         Ok(A)
//!     #     }
//!     # }
//!     #
//!     # impl<'env> TryIntoJavaValue<'env> for &A {
//!     #   type Target = JObject<'env>;
//!     #
//!     #   fn try_into(self, env: &JNIEnv<'env>) -> ::robusta_jni::jni::errors::Result<Self::Target> {
//!     #         env.new_object("A", "()V", &[])
//!     #   }
//!     # }
//!     #
//! impl A {
//!     pub extern "java" fn selfMethod(
//!         &self,
//!         env: &JNIEnv,
//!         i: i32,
//!         u: i32,
//!     ) -> ::robusta_jni::jni::errors::Result<i32> {}
//! }
//! # }
//! ```
//!
//! ## Constructors
//!
//! Example:
//! ```rust
//! # use robusta_jni::bridge;
//! # use robusta_jni::convert::{Signature, TryFromJavaValue};
//! #
//! # #[bridge]
//! # mod jni {
//!     # use robusta_jni::convert::{Signature, TryFromJavaValue, JavaValue};
//!     # use robusta_jni::jni::JNIEnv;
//!     # use jni::objects::JObject;
//!     # #[derive(Signature)]
//!     # #[package()]
//!     # struct A;
//!     #
//!     # impl<'env: 'borrow, 'borrow> TryFromJavaValue<'env, 'borrow> for A {
//!     #    type Source = JObject<'env>;
//!     #
//!     #    fn try_from(s: Self::Source,env: &'borrow JNIEnv<'env>) -> ::robusta_jni::jni::errors::Result<Self> {
//!     #         Ok(A)
//!     #     }
//!     # }
//!     #
//! impl A {
//!     #[constructor]  //   vvv ---- this method name can be anything because it's a constructor
//!     pub extern "java" fn new(
//!         env: &JNIEnv
//!     ) -> ::robusta_jni::jni::errors::Result<Self> {}
//! }
//! # }
//! ```
//!
//! # Conversion details and special lifetimes
//! The procedural macro handles two special lifetimes specially: `'env` and `'borrow`.
//!
//! When declaring structs with lifetimes you may be asked to name one of the lifetimes as `'env` in order to
//! disambiguate code generation for the attribute macro.
//! In the generated code, this lifetime would correspond to the one used to convert your type to `*IntoJavaValue`, like:
//! ```ignore
//! <A<'env> as TryIntoJavaValue<'env>>
//! ```
//! This lifetime is always used as the lifetime parameter of `JNIEnv` instances.
//!
//! When using `*FromJavaValue` derive macros your structs will be required to have both `'env` and `'borrow`,
//! with the same bounds as in the trait definition. For more information, see the relevant traits documentation.
//!
//! ## Raising exceptions
//! You can make a Rust native method raise a Java exception simply by returning a [`jni::errors::Result`] with an `Err` variant.
//! See the [`convert`] module documentation for more information.
//!
//! ## Library-provided conversions
//!
//! | **Rust**                                                                           | **Java**                          |
//! |------------------------------------------------------------------------------------|-----------------------------------|
//! | i32                                                                                | int                               |
//! | bool                                                                               | boolean                           |
//! | char                                                                               | char                              |
//! | i8                                                                                 | byte                              |
//! | f32                                                                                | float                             |
//! | f64                                                                                | double                            |
//! | i64                                                                                | long                              |
//! | i16                                                                                | short                             |
//! | String                                                                             | String                            |
//! | Vec\<T\>†                                                                          | ArrayList\<T\>                    |
//! | Box<[u8]>                                                                          | byte[]                            |
//! | [jni::JObject<'env>](jni::objects::JObject)                                      ‡ | *(any Java object as input type)* |
//! | [jni::jobject](jni::sys::jobject)                                                    | *(any Java object as output)*     |
//!
//! † Type parameter `T` must implement proper conversion types
//!
//! ‡ The special `'env` lifetime **must** be used
//!
//! ## Limitations
//!
//! Currently there are some limitations in the conversion mechanism:
//!  * Boxed types are supported only through the opaque `JObject`/`jobject` types
//!  * Automatic type conversion is limited to the table outlined above, though easily extendable if needed.
//!
//! [`Signature`]: convert::Signature
//! [`JNIEnv`]: jni::JNIEnv
//!

pub use robusta_codegen::bridge;

pub mod convert;

pub use jni;

pub use static_assertions::assert_type_eq_all;



================================================
FILE: src/convert/field.rs
================================================
use std::marker::PhantomData;
use std::str::FromStr;

use std::convert::{TryFrom, TryInto};

use jni::errors::Error as JniError;
use jni::errors::Result as JniResult;
use jni::objects::{JFieldID, JObject};
use jni::signature::ReturnType;
use jni::JNIEnv;

use crate::convert::{
    FromJavaValue, IntoJavaValue, JValueWrapper, JavaValue, Signature, TryFromJavaValue,
    TryIntoJavaValue,
};
use crate::jni::objects::JValue;

#[derive(Clone)]
pub struct Field<'env: 'borrow, 'borrow, T>
where
    T: Signature,
{
    env: &'borrow JNIEnv<'env>,
    field_id: JFieldID,
    obj: JObject<'env>,
    marker: PhantomData<T>,
}

impl<'env: 'borrow, 'borrow, T> Field<'env, 'borrow, T>
where
    T: Signature,
{
    pub fn new(
        env: &'borrow JNIEnv<'env>,
        obj: JObject<'env>,
        classpath_path: &str,
        field_name: &str,
    ) -> Option<Self> {
        let field_id = env
            .get_field_id(classpath_path, field_name, <T as Signature>::SIG_TYPE)
            .ok()?;

        Some(Field {
            env,
            field_id,
            obj,
            marker: Default::default(),
        })
    }
}

impl<'env: 'borrow, 'borrow, T> Field<'env, 'borrow, T>
where
    T: Signature + TryIntoJavaValue<'env> + TryFromJavaValue<'env, 'borrow>,
    <T as TryFromJavaValue<'env, 'borrow>>::Source: TryFrom<JValueWrapper<'env>, Error = JniError>,
    JValue<'env>: From<<T as TryIntoJavaValue<'env>>::Target>,
{
    pub fn set(&mut self, value: T) -> JniResult<()> {
        let v = TryIntoJavaValue::try_into(value, self.env)?;
        let jvalue: JValue = JValue::from(v);

        self.env
            .set_field_unchecked(self.obj, self.field_id, jvalue)?;
        Ok(())
    }

    pub fn get(&self) -> JniResult<T> {
        let res: JValue = self.env.get_field_unchecked(
            self.obj,
            self.field_id,
            ReturnType::from_str(<T as Signature>::SIG_TYPE).unwrap(),
        )?;

        let f = JValueWrapper::from(res);
        TryInto::try_into(f).and_then(|v| TryFromJavaValue::try_from(v, &self.env))
    }

    // Java object is not sufficient to retrieve parent object / field owner
    // We can use the owner as the source instead, but we don't have neither the field name nor the class classpath path
    // Don't implement this and use `#[field]` attribute instead?
    // A nicer solution would be to have a `const CLASS_PATH: &str` and a `const FIELD_NAME: &str` const parameters and use those instead,
    // but full const generics are required for that.
    // FIXME: use const generics to parametrize `Field` by class path and field name, and implement `(Try)FromJavaValue`
    pub fn field_try_from(
        source: JObject<'env>,
        classpath_path: &str,
        field_name: &str,
        env: &'borrow JNIEnv<'env>,
    ) -> JniResult<Self> {
        let class = env.find_class(classpath_path)?;
        let field_id = env.get_field_id(class, field_name, <T as Signature>::SIG_TYPE)?;

        Ok(Self {
            env,
            field_id,
            obj: source.autobox(env),
            marker: Default::default(),
        })
    }
}

impl<'env: 'borrow, 'borrow, T> Field<'env, 'borrow, T>
where
    T: Signature + IntoJavaValue<'env> + FromJavaValue<'env, 'borrow>,
    <T as FromJavaValue<'env, 'borrow>>::Source: TryFrom<JValueWrapper<'env>, Error = JniError>,
    JValue<'env>: From<<T as IntoJavaValue<'env>>::Target>,
{
    pub fn set_unchecked(&mut self, value: T) {
        let v = IntoJavaValue::into(value, self.env);
        let jvalue = JValue::from(v);

        self.env
            .set_field_unchecked(self.obj, self.field_id, jvalue)
            .unwrap();
    }

    pub fn get_unchecked(&self) -> T {
        let res = self
            .env
            .get_field_unchecked(
                self.obj,
                self.field_id,
                ReturnType::from_str(<T as Signature>::SIG_TYPE).unwrap(),
            )
            .unwrap();

        TryInto::try_into(JValueWrapper::from(res))
            .map(|v| FromJavaValue::from(v, &self.env))
            .unwrap()
    }

    pub fn field_from(
        source: JObject<'env>,
        classpath_path: &str,
        field_name: &str,
        env: &'borrow JNIEnv<'env>,
    ) -> Self {
        let class = env.find_class(classpath_path).unwrap();
        let field_id = env
            .get_field_id(class, field_name, <T as Signature>::SIG_TYPE)
            .unwrap();

        Self {
            env,
            field_id,
            obj: source.autobox(env),
            marker: Default::default(),
        }
    }
}

impl<'env: 'borrow, 'borrow, T> Signature for Field<'env, 'borrow, T>
where
    T: Signature,
{
    const SIG_TYPE: &'static str = <T as Signature>::SIG_TYPE;
}



================================================
FILE: src/convert/mod.rs
================================================
//! Conversion facilities.
//! This module provides two trait families: [FromJavaValue]/[IntoJavaValue] (infallible conversions) and [TryFromJavaValue]/[TryIntoJavaValue] (fallible conversions),
//! similar to the ones found in the standard library.
//!
//! The `call_type` attribute controls which of the two conversion families is selected during code generation.
//! `call_type` is a per-function attribute.
//! Specific parameters that can be given to `call_type` can be found in the module documentation relative to the trait family ([safe] module for fallible conversions and [unchecked] module for infallible conversions)
//!
//! **If the `call_type` attribute is omitted, the fallible conversion trait family is chosen.**
//!
//! Example usage:
//! ```
//! use robusta_jni::bridge;
//!
//! #[bridge]
//! mod jni {
//!     #[package(com.example.robusta)]
//!     struct HelloWorld;
//!
//!     impl HelloWorld {
//!         #[call_type(unchecked)]
//!         pub extern "jni" fn special(mut input1: Vec<i32>, input2: i32) -> Vec<String> {
//!             input1.push(input2);
//!             input1.iter().map(ToString::to_string).collect()
//!         }
//!
//!         #[call_type(safe(exception_class = "java.lang.IllegalArgumentException", message = "invalid value"))]
//!         pub extern "jni" fn bar(foo: i32) -> ::robusta_jni::jni::errors::Result<i32> { Ok(foo) }
//!     }
//! }
//! ```
//!
//! # Raising exceptions from native code
//! If you want to have the option of throwing a Java exception from native code (conversion errors aside), you can
//! annotate your function signature with a [`jni::errors::Result<T>`] return type.
//!
//! When used with `#[call_type(safe)]`, if an `Err` is returned a Java exception is thrown (the one specified in the `call_type` attribute,
//! or `java.lang.RuntimeException` if omitted).
//!

use std::convert::TryFrom;
use std::str::FromStr;

use jni::errors::Error;
use jni::objects::{JObject, JString, JValue};
use jni::signature::ReturnType;
use jni::sys::{jboolean, jbyte, jchar, jdouble, jfloat, jint, jlong, jobject, jshort};
use jni::JNIEnv;
use paste::paste;

pub use field::*;
pub use robusta_codegen::Signature;
pub use safe::*;
pub use unchecked::*;

pub mod field;
pub mod safe;
pub mod unchecked;

/// A trait for types that are ffi-safe to use with JNI. It is implemented for primitives, [JObject](jni::objects::JObject) and [jobject](jni::sys::jobject).
/// Users that want automatic conversion should instead implement [FromJavaValue], [IntoJavaValue] and/or [TryFromJavaValue], [TryIntoJavaValue]
pub trait JavaValue<'env> {
    /// Convert instance to a [`JObject`].
    fn autobox(self, env: &JNIEnv<'env>) -> JObject<'env>;

    /// Convert [`JObject`] to the implementing type.
    fn unbox(s: JObject<'env>, env: &JNIEnv<'env>) -> Self;
}

/// This trait provides [type signatures](https://docs.oracle.com/en/java/javase/15/docs/specs/jni/types.html#type-signatures) for types.
/// It is necessary to support conversions to/from Java types.
///
/// While you can implement this trait manually, you should probably use the derive macro.
///
/// The derive macro requires a `#[package()]` attribute on implementing structs (most likely you already have that).
///
pub trait Signature {
    /// [Java type signature](https://docs.oracle.com/en/java/javase/15/docs/specs/jni/types.html#type-signatures) for the implementing type.
    const SIG_TYPE: &'static str;
}

macro_rules! jvalue_types {
    ($type:ty: $boxed:ident ($sig:ident) [$unbox_method:ident]) => {
        impl Signature for $type {
            const SIG_TYPE: &'static str = stringify!($sig);
        }

        impl<'env> JavaValue<'env> for $type {
            fn autobox(self, env: &JNIEnv<'env>) -> JObject<'env> {
                env.call_static_method_unchecked(concat!("java/lang/", stringify!($boxed)),
                    (concat!("java/lang/", stringify!($boxed)), "valueOf", concat!(stringify!(($sig)), "Ljava/lang/", stringify!($boxed), ";")),
                    ReturnType::from_str(concat!("Ljava/lang/", stringify!($boxed), ";")).unwrap(),
                    &[JValue::from(self).to_jni()]).unwrap().l().unwrap()
            }

            fn unbox(s: JObject<'env>, env: &JNIEnv<'env>) -> Self {
                paste!(Into::into(env.call_method_unchecked(s, (concat!("java/lang/", stringify!($boxed)), stringify!($unbox_method), concat!("()", stringify!($sig))), ReturnType::from_str(stringify!($sig)).unwrap(), &[])
                    .unwrap().[<$sig:lower>]()
                    .unwrap()))
            }
        }
    };

    ($type:ty: $boxed:ident ($sig:ident) [$unbox_method:ident], $($rest:ty: $rest_boxed:ident ($rest_sig:ident) [$unbox_method_rest:ident]),+) => {
        jvalue_types!($type: $boxed ($sig) [$unbox_method]);

        jvalue_types!($($rest: $rest_boxed ($rest_sig) [$unbox_method_rest]),+);
    }
}

jvalue_types! {
    jboolean: Boolean (Z) [booleanValue],
    jbyte: Byte (B) [byteValue],
    jchar: Character (C) [charValue],
    jdouble: Double (D) [doubleValue],
    jfloat: Float (F) [floatValue],
    jint: Integer (I) [intValue],
    jlong: Long (J) [longValue],
    jshort: Short (S) [shortValue]
}

impl Signature for () {
    const SIG_TYPE: &'static str = "V";
}

impl<'env> JavaValue<'env> for () {
    fn autobox(self, _env: &JNIEnv<'env>) -> JObject<'env> {
        panic!("called `JavaValue::autobox` on unit value")
    }

    fn unbox(_s: JObject<'env>, _env: &JNIEnv<'env>) -> Self {}
}

impl<'env> Signature for JObject<'env> {
    const SIG_TYPE: &'static str = "Ljava/lang/Object;";
}

impl<'env> JavaValue<'env> for JObject<'env> {
    fn autobox(self, _env: &JNIEnv<'env>) -> JObject<'env> {
        self
    }

    fn unbox(s: JObject<'env>, _env: &JNIEnv<'env>) -> Self {
        s
    }
}

impl<'env> JavaValue<'env> for jobject {
    fn autobox(self, _env: &JNIEnv<'env>) -> JObject<'env> {
        unsafe { JObject::from_raw(self) }
    }

    fn unbox(s: JObject<'env>, _env: &JNIEnv<'env>) -> Self {
        s.into_raw()
    }
}

impl<'env> Signature for JString<'env> {
    const SIG_TYPE: &'static str = "Ljava/lang/String;";
}

impl<'env> JavaValue<'env> for JString<'env> {
    fn autobox(self, _env: &JNIEnv<'env>) -> JObject<'env> {
        Into::into(self)
    }

    fn unbox(s: JObject<'env>, _env: &JNIEnv<'env>) -> Self {
        From::from(s)
    }
}

impl<T: Signature> Signature for jni::errors::Result<T> {
    const SIG_TYPE: &'static str = <T as Signature>::SIG_TYPE;
}

pub struct JValueWrapper<'a>(pub JValue<'a>);

impl<'a> From<JValue<'a>> for JValueWrapper<'a> {
    fn from(v: JValue<'a>) -> Self {
        JValueWrapper(v)
    }
}

impl<'a> From<JValueWrapper<'a>> for JValue<'a> {
    fn from(v: JValueWrapper<'a>) -> Self {
        v.0
    }
}

impl<'a> TryFrom<JValueWrapper<'a>> for jboolean {
    type Error = jni::errors::Error;

    fn try_from(value: JValueWrapper<'a>) -> Result<Self, Self::Error> {
        match value.0 {
            JValue::Bool(b) => Ok(b),
            _ => Err(Error::WrongJValueType("bool", value.0.type_name()).into()),
        }
    }
}

impl<'a> TryFrom<JValueWrapper<'a>> for jbyte {
    type Error = jni::errors::Error;

    fn try_from(value: JValueWrapper<'a>) -> Result<Self, Self::Error> {
        match value.0 {
            JValue::Byte(b) => Ok(b),
            _ => Err(Error::WrongJValueType("byte", value.0.type_name()).into()),
        }
    }
}

impl<'a> TryFrom<JValueWrapper<'a>> for jchar {
    type Error = jni::errors::Error;

    fn try_from(value: JValueWrapper<'a>) -> Result<Self, Self::Error> {
        match value.0 {
            JValue::Char(c) => Ok(c),
            _ => Err(Error::WrongJValueType("char", value.0.type_name()).into()),
        }
    }
}

impl<'a> TryFrom<JValueWrapper<'a>> for jdouble {
    type Error = jni::errors::Error;

    fn try_from(value: JValueWrapper<'a>) -> Result<Self, Self::Error> {
        match value.0 {
            JValue::Double(d) => Ok(d),
            _ => Err(Error::WrongJValueType("double", value.0.type_name()).into()),
        }
    }
}

impl<'a> TryFrom<JValueWrapper<'a>> for jfloat {
    type Error = jni::errors::Error;

    fn try_from(value: JValueWrapper<'a>) -> Result<Self, Self::Error> {
        match value.0 {
            JValue::Float(f) => Ok(f),
            _ => Err(Error::WrongJValueType("float", value.0.type_name()).into()),
        }
    }
}

impl<'a> TryFrom<JValueWrapper<'a>> for jint {
    type Error = jni::errors::Error;

    fn try_from(value: JValueWrapper<'a>) -> Result<Self, Self::Error> {
        match value.0 {
            JValue::Int(i) => Ok(i),
            _ => Err(Error::WrongJValueType("int", value.0.type_name()).into()),
        }
    }
}

impl<'a> TryFrom<JValueWrapper<'a>> for jshort {
    type Error = jni::errors::Error;

    fn try_from(value: JValueWrapper<'a>) -> Result<Self, Self::Error> {
        match value.0 {
            JValue::Short(s) => Ok(s),
            _ => Err(Error::WrongJValueType("short", value.0.type_name()).into()),
        }
    }
}

impl<'a> TryFrom<JValueWrapper<'a>> for jlong {
    type Error = jni::errors::Error;

    fn try_from(value: JValueWrapper<'a>) -> Result<Self, Self::Error> {
        match value.0 {
            JValue::Long(l) => Ok(l),
            _ => Err(Error::WrongJValueType("long", value.0.type_name()).into()),
        }
    }
}

impl<'a> TryFrom<JValueWrapper<'a>> for () {
    type Error = jni::errors::Error;

    fn try_from(value: JValueWrapper<'a>) -> Result<Self, Self::Error> {
        match value.0 {
            JValue::Void => Ok(()),
            _ => Err(Error::WrongJValueType("void", value.0.type_name()).into()),
        }
    }
}

impl<'a> TryFrom<JValueWrapper<'a>> for JObject<'a> {
    type Error = jni::errors::Error;

    fn try_from(value: JValueWrapper<'a>) -> Result<Self, Self::Error> {
        match value.0 {
            JValue::Object(o) => Ok(o),
            _ => Err(Error::WrongJValueType("object", value.0.type_name()).into()),
        }
    }
}

impl<'a> TryFrom<JValueWrapper<'a>> for JString<'a> {
    type Error = jni::errors::Error;

    fn try_from(value: JValueWrapper<'a>) -> Result<Self, Self::Error> {
        match value.0 {
            JValue::Object(o) => Ok(From::from(o)),
            _ => Err(Error::WrongJValueType("string", value.0.type_name()).into()),
        }
    }
}



================================================
FILE: src/convert/safe.rs
================================================
//! Fallible conversions traits.
//!
//! These are the traits selected if `call_type` is omitted or if specified with a `safe` parameter.
//!
//! ```ignore
//! #[call_type(safe)]
//! ```
//!
//! If any conversion fails, e.g. while converting input parameters or return arguments a Java exception is thrown.
//! Exception class and exception message can be customized with the `exception_class` and `message` parameters of the `safe` option, as such:
//!
//! ```ignore
//! #[call_type(safe(exception_class = "java.io.IOException", message = "Error while calling JNI function!"))]
//! ```
//!
//! Both of these parameters are optional. By default, the exception class is `java.lang.RuntimeException`.
//!

use jni::errors::{Error, Result};
use jni::objects::{JList, JObject, JString, JValue};
use jni::sys::{jboolean, jbooleanArray, jbyteArray, jchar, jobject};
use jni::JNIEnv;

use crate::convert::unchecked::{FromJavaValue, IntoJavaValue};
use crate::convert::{JavaValue, Signature};

pub use robusta_codegen::{TryFromJavaValue, TryIntoJavaValue};

/// Conversion trait from Rust values to Java values, analogous to [TryInto](std::convert::TryInto). Used when converting types returned from JNI-available functions.
///
/// This is the default trait used when converting values from Rust to Java.
///
/// # Notes on derive macro
/// The same notes on [`TryFromJavaValue`] apply.
///
/// Note that when autoderiving `TryIntoJavaValue` for `T`, an implementation for all of `T`, `&T` and `&mut T` is generated (for ergonomics).
///
pub trait TryIntoJavaValue<'env>: Signature {
    /// Conversion target type.
    type Target: JavaValue<'env>;

    /// [Signature](https://docs.oracle.com/en/java/javase/15/docs/specs/jni/types.html#type-signatures) of the source type.
    /// By default, use the one defined on the [`Signature`] trait for the implementing type.
    const SIG_TYPE: &'static str = <Self as Signature>::SIG_TYPE;

    /// Perform the conversion.
    fn try_into(self, env: &JNIEnv<'env>) -> Result<Self::Target>;
}

/// Conversion trait from Java values to Rust values, analogous to [TryFrom](std::convert::TryInto). Used when converting types that are input to JNI-available functions.
///
/// This is the default trait used when converting values from Java to Rust.
///
/// # Notes on the derive macro
/// When using the derive macro, the deriving struct **must** have a [`AutoLocal`] field annotated with both `'env` and `'borrow` lifetimes and a `#[instance]` attribute.
/// This fields keeps a [local reference](https://docs.oracle.com/en/java/javase/15/docs/specs/jni/design.html#global-and-local-references) to the underlying Java object.
/// All other fields are automatically initialized from fields on the Java instance with the same name.
///
/// Example:
///
/// ```rust
/// # use robusta_jni::bridge;
/// use robusta_jni::convert::{Signature, TryFromJavaValue};
/// use robusta_jni::jni::objects::AutoLocal;
/// #
/// # #[bridge]
/// # mod jni {
///     # use robusta_jni::convert::{Signature, TryFromJavaValue};
///     # use robusta_jni::jni::JNIEnv;
///     # use jni::objects::{JObject, AutoLocal};
///
/// #[derive(Signature, TryFromJavaValue)]
/// #[package()]
/// struct A<'env: 'borrow, 'borrow> {
///     #[instance]
///     raw: AutoLocal<'env, 'borrow>,
///     foo: i32
/// }
/// # }
/// ```
///
/// [`AutoLocal`]: jni::objects::AutoLocal
///
pub trait TryFromJavaValue<'env: 'borrow, 'borrow>
where
    Self: Sized + Signature,
{
    /// Conversion source type.
    type Source: JavaValue<'env>;

    /// [Signature](https://docs.oracle.com/en/java/javase/15/docs/specs/jni/types.html#type-signatures) of the target type.
    /// By default, use the one defined on the [`Signature`] trait for the implementing type.
    const SIG_TYPE: &'static str = <Self as Signature>::SIG_TYPE;

    /// Perform the conversion.
    fn try_from(s: Self::Source, env: &'borrow JNIEnv<'env>) -> Result<Self>;
}

impl<'env, T> TryIntoJavaValue<'env> for T
where
    T: JavaValue<'env> + Signature,
{
    type Target = T;

    fn try_into(self, env: &JNIEnv<'env>) -> Result<Self::Target> {
        Ok(IntoJavaValue::into(self, env))
    }
}

impl<'env: 'borrow, 'borrow, T> TryFromJavaValue<'env, 'borrow> for T
where
    T: JavaValue<'env> + Signature,
{
    type Source = T;

    fn try_from(s: Self::Source, env: &'borrow JNIEnv<'env>) -> Result<Self> {
        Ok(FromJavaValue::from(s, env))
    }
}

impl<'env> TryIntoJavaValue<'env> for String {
    type Target = JString<'env>;
    const SIG_TYPE: &'static str = "Ljava/lang/String;";

    fn try_into(self, env: &JNIEnv<'env>) -> Result<Self::Target> {
        env.new_string(self)
    }
}

impl<'env: 'borrow, 'borrow> TryFromJavaValue<'env, 'borrow> for String {
    type Source = JString<'env>;

    fn try_from(s: Self::Source, env: &'borrow JNIEnv<'env>) -> Result<Self> {
        env.get_string(s).map(Into::into)
    }
}

impl<'env> TryIntoJavaValue<'env> for bool {
    type Target = jboolean;

    fn try_into(self, _env: &JNIEnv<'env>) -> Result<Self::Target> {
        Ok(IntoJavaValue::into(self, _env))
    }
}

impl<'env: 'borrow, 'borrow> TryFromJavaValue<'env, 'borrow> for bool {
    type Source = jboolean;

    fn try_from(s: Self::Source, _env: &JNIEnv<'env>) -> Result<Self> {
        Ok(FromJavaValue::from(s, _env))
    }
}

impl<'env> TryIntoJavaValue<'env> for char {
    type Target = jchar;

    fn try_into(self, _env: &JNIEnv<'env>) -> Result<Self::Target> {
        Ok(IntoJavaValue::into(self, _env))
    }
}

impl<'env: 'borrow, 'borrow> TryFromJavaValue<'env, 'borrow> for char {
    type Source = jchar;

    fn try_from(s: Self::Source, _env: &JNIEnv<'env>) -> Result<Self> {
        let res = std::char::decode_utf16(std::iter::once(s)).next();

        match res {
            Some(Ok(c)) => Ok(c),
            Some(Err(_)) | None => Err(Error::WrongJValueType("char", "jchar")),
        }
    }
}

impl Signature for Box<[bool]> {
    const SIG_TYPE: &'static str = "[Z";
}

impl<'env> TryIntoJavaValue<'env> for Box<[bool]> {
    type Target = jbooleanArray;

    fn try_into(self, env: &JNIEnv<'env>) -> Result<Self::Target> {
        let len = self.len();
        let buf: Vec<_> = self.iter().map(|&b| Into::into(b)).collect();
        let raw = env.new_boolean_array(len as i32)?;
        env.set_boolean_array_region(raw, 0, &buf)?;
        Ok(raw)
    }
}

impl<'env: 'borrow, 'borrow> TryFromJavaValue<'env, 'borrow> for Box<[bool]> {
    type Source = jbooleanArray;

    fn try_from(s: Self::Source, env: &'borrow JNIEnv<'env>) -> Result<Self> {
        let len = env.get_array_length(s)?;
        let mut buf = Vec::with_capacity(len as usize).into_boxed_slice();
        env.get_boolean_array_region(s, 0, &mut *buf)?;

        buf.iter()
            .map(|&b| TryFromJavaValue::try_from(b, &env))
            .collect()
    }
}

impl<'env, T> TryIntoJavaValue<'env> for Vec<T>
where
    T: TryIntoJavaValue<'env>,
{
    type Target = jobject;

    fn try_into(self, env: &JNIEnv<'env>) -> Result<Self::Target> {
        let obj = env.new_object(
            "java/util/ArrayList",
            "(I)V",
            &[JValue::Int(self.len() as i32)],
        )?;
        let list = JList::from_env(&env, obj)?;

        let _: Result<Vec<_>> = self
            .into_iter()
            .map::<Result<_>, _>(|el| {
                Ok(JavaValue::autobox(
                    TryIntoJavaValue::try_into(el, &env)?,
                    &env,
                ))
            })
            .map(|el| Ok(list.add(el?)))
            .collect();

        Ok(list.into_raw())
    }
}

impl<'env: 'borrow, 'borrow, T, U> TryFromJavaValue<'env, 'borrow> for Vec<T>
where
    T: TryFromJavaValue<'env, 'borrow, Source = U>,
    U: JavaValue<'env>,
{
    type Source = JObject<'env>;

    fn try_from(s: Self::Source, env: &'borrow JNIEnv<'env>) -> Result<Self> {
        let list = JList::from_env(env, s)?;

        list.iter()?
            .map(|el| T::try_from(U::unbox(el, env), env))
            .collect()
    }
}

impl Signature for Box<[u8]> {
    const SIG_TYPE: &'static str = "[B";
}

impl<'env> TryIntoJavaValue<'env> for Box<[u8]> {
    type Target = jbyteArray;

    fn try_into(self, env: &JNIEnv<'env>) -> Result<Self::Target> {
        env.byte_array_from_slice(self.as_ref())
    }
}

impl<'env: 'borrow, 'borrow> TryFromJavaValue<'env, 'borrow> for Box<[u8]> {
    type Source = jbyteArray;

    fn try_from(s: Self::Source, env: &'borrow JNIEnv<'env>) -> Result<Box<[u8]>> {
        let buf = env.convert_byte_array(s)?;
        let boxed_slice = buf.into_boxed_slice();
        Ok(boxed_slice)
    }
}

/// When returning a [`jni::errors::Result`], if the returned variant is `Ok(v)` then the value `v` is returned as usual.
///
/// If the returned value is `Err`, the Java exception specified in the `#[call_type(safe)]` attribute is thrown
/// (by default `java.lang.RuntimeException`)
impl<'env, T> TryIntoJavaValue<'env> for jni::errors::Result<T>
where
    T: TryIntoJavaValue<'env>,
{
    type Target = <T as TryIntoJavaValue<'env>>::Target;

    fn try_into(self, env: &JNIEnv<'env>) -> Result<Self::Target> {
        self.and_then(|s| TryIntoJavaValue::try_into(s, env))
    }
}



================================================
FILE: src/convert/unchecked.rs
================================================
//! Infallible conversion traits.
//!
//! These traits allow for a leaner generated glue code, with possibly some performance benefits.
//!
//! These conversion traits can be enabled to be used during code generation with the `unchecked` option on the `call_type` attribute, as so:
//!
//! ```ignore
//! #[call_type(unchecked)]
//! ```
//!
//! **These functions *will* panic should any conversion fail.**
//!

use jni::objects::{JList, JObject, JString, JValue};
use jni::sys::{jboolean, jbooleanArray, jchar, jobject, jstring};
use jni::JNIEnv;

use crate::convert::{JavaValue, Signature};

pub use robusta_codegen::{FromJavaValue, IntoJavaValue};

/// Conversion trait from Rust values to Java values, analogous to [Into]. Used when converting types returned from JNI-available functions.
///
/// The usage of this trait in the generated code can be enabled with the `#[call_type(unchecked)]` attribute on a per-method basis.
///
/// When using this trait the conversion is assumed to be infallible.
/// Should a conversion fail, a panic will be raised.
///
/// # Notes on the derive macro
///
/// The same notes on [`TryIntoJavaValue`] apply.
///
/// [`TryIntoJavaValue`]: crate::convert::TryIntoJavaValue
///
pub trait IntoJavaValue<'env>: Signature {
    /// Conversion target type.
    type Target: JavaValue<'env>;

    /// [Signature](https://docs.oracle.com/en/java/javase/15/docs/specs/jni/types.html#type-signatures) of the source type.
    /// By default, use the one defined on the [`Signature`] trait for the implementing type.
    const SIG_TYPE: &'static str = <Self as Signature>::SIG_TYPE;

    /// Perform the conversion.
    fn into(self, env: &JNIEnv<'env>) -> Self::Target;
}

/// Conversion trait from Java values to Rust values, analogous to [From]. Used when converting types that are input to JNI-available functions.
///
/// # Notes on derive macro
///
/// The same notes on [`TryFromJavaValue`] apply.
///
/// [`TryFromJavaValue`]: crate::convert::TryFromJavaValue
///
pub trait FromJavaValue<'env: 'borrow, 'borrow>: Signature {
    /// Conversion source type.
    type Source: JavaValue<'env>;

    /// [Signature](https://docs.oracle.com/en/java/javase/15/docs/specs/jni/types.html#type-signatures) of the target type.
    /// By default, use the one defined on the [`Signature`] trait for the implementing type.
    const SIG_TYPE: &'static str = <Self as Signature>::SIG_TYPE;

    /// Perform the conversion.
    fn from(s: Self::Source, env: &'borrow JNIEnv<'env>) -> Self;
}

impl<'env, T> IntoJavaValue<'env> for T
where
    T: JavaValue<'env> + Signature,
{
    type Target = T;

    fn into(self, _: &JNIEnv<'env>) -> Self::Target {
        self
    }
}

impl<'env: 'borrow, 'borrow, T> FromJavaValue<'env, 'borrow> for T
where
    T: JavaValue<'env> + Signature,
{
    type Source = T;

    fn from(t: Self::Source, _: &'borrow JNIEnv<'env>) -> Self {
        t
    }
}

impl Signature for String {
    const SIG_TYPE: &'static str = "Ljava/lang/String;";
}

impl<'env> IntoJavaValue<'env> for String {
    type Target = jstring;

    fn into(self, env: &JNIEnv<'env>) -> Self::Target {
        env.new_string(self).unwrap().into_raw()
    }
}

impl<'env: 'borrow, 'borrow> FromJavaValue<'env, 'borrow> for String {
    type Source = JString<'env>;

    fn from(s: Self::Source, env: &'borrow JNIEnv<'env>) -> Self {
        env.get_string(s).unwrap().into()
    }
}

impl<'env> IntoJavaValue<'env> for bool {
    type Target = jboolean;

    fn into(self, _env: &JNIEnv<'env>) -> Self::Target {
        if self {
            1
        } else {
            0
        }
    }
}

impl Signature for bool {
    const SIG_TYPE: &'static str = <jboolean as Signature>::SIG_TYPE;
}

impl<'env: 'borrow, 'borrow> FromJavaValue<'env, 'borrow> for bool {
    type Source = jboolean;

    fn from(s: Self::Source, _env: &JNIEnv<'env>) -> Self {
        s == 1
    }
}

impl Signature for char {
    const SIG_TYPE: &'static str = <jchar as Signature>::SIG_TYPE;
}

impl<'env> IntoJavaValue<'env> for char {
    type Target = jchar;

    fn into(self, _env: &JNIEnv<'env>) -> Self::Target {
        self as jchar
    }
}

impl<'env: 'borrow, 'borrow> FromJavaValue<'env, 'borrow> for char {
    type Source = jchar;

    fn from(s: Self::Source, _env: &JNIEnv<'env>) -> Self {
        std::char::decode_utf16(std::iter::once(s))
            .next()
            .unwrap()
            .unwrap()
    }
}

impl<'env> IntoJavaValue<'env> for Box<[bool]> {
    type Target = jbooleanArray;

    fn into(self, env: &JNIEnv<'env>) -> Self::Target {
        let len = self.len();
        let buf: Vec<_> = self.iter().map(|&b| Into::into(b)).collect();
        let raw = env.new_boolean_array(len as i32).unwrap();
        env.set_boolean_array_region(raw, 0, &buf).unwrap();
        raw
    }
}

impl<'env: 'borrow, 'borrow> FromJavaValue<'env, 'borrow> for Box<[bool]> {
    type Source = jbooleanArray;

    fn from(s: Self::Source, env: &'borrow JNIEnv<'env>) -> Self {
        let len = env.get_array_length(s).unwrap();
        let mut buf = Vec::with_capacity(len as usize).into_boxed_slice();
        env.get_boolean_array_region(s, 0, &mut *buf).unwrap();

        buf.iter().map(|&b| FromJavaValue::from(b, &env)).collect()
    }
}

impl<T> Signature for Vec<T> {
    const SIG_TYPE: &'static str = "Ljava/util/ArrayList;";
}

impl<'env, T> IntoJavaValue<'env> for Vec<T>
where
    T: IntoJavaValue<'env>,
{
    type Target = jobject;

    fn into(self, env: &JNIEnv<'env>) -> Self::Target {
        let obj = env
            .new_object(
                "java/util/ArrayList",
                "(I)V",
                &[JValue::Int(self.len() as i32)],
            )
            .unwrap();
        let list = JList::from_env(&env, obj).unwrap();

        self.into_iter()
            .map(|el| JavaValue::autobox(IntoJavaValue::into(el, &env), &env))
            .for_each(|el| {
                list.add(el).unwrap();
            });

        list.into_raw()
    }
}

impl<'env: 'borrow, 'borrow, T, U> FromJavaValue<'env, 'borrow> for Vec<T>
where
    T: FromJavaValue<'env, 'borrow, Source = U>,
    U: JavaValue<'env>,
{
    type Source = JObject<'env>;

    fn from(s: Self::Source, env: &'borrow JNIEnv<'env>) -> Self {
        let list = JList::from_env(env, s).unwrap();

        list.iter()
            .unwrap()
            .map(|el| T::from(U::unbox(el, env), env))
            .collect()
    }
}

impl<'env, T> IntoJavaValue<'env> for jni::errors::Result<T>
where
    T: IntoJavaValue<'env>,
{
    type Target = <T as IntoJavaValue<'env>>::Target;

    fn into(self, env: &JNIEnv<'env>) -> Self::Target {
        self.map(|s| IntoJavaValue::into(s, env)).unwrap()
    }
}



================================================
FILE: tests/mod.rs
================================================
use std::fs;
use std::path::Path;
use jni::objects::JString;
use native::jni::User;
use robusta_jni::convert::FromJavaValue;
use robusta_jni::jni::{InitArgsBuilder, JNIEnv, JavaVM};
use std::process::Command;

fn print_exception(env: &JNIEnv) -> jni::errors::Result<()> {
    let ex = env.exception_occurred()?;
    env.exception_clear()?;
    let res = env.call_method(ex, "toString", "()Ljava/lang/String;", &[])?;
    let message: JString = From::from(res.l()?);
    let s: String = FromJavaValue::from(message, env);
    println!("Java exception occurred: {}", s);
    Ok(())
}

#[test]
fn java_integration_tests() {
    let mut child = Command::new(
        fs::canonicalize(
            Path::new(".").join("tests").join("driver").join(
                if cfg!(target_os = "windows") { "gradlew.bat" } else { "gradlew" })
        ).expect("Gradle not found"))
        .args(&["test", "-i"])
        .current_dir(
            Path::new(".").join("tests").join("driver").to_str().expect("Failed to get driver path")
        )
        .spawn()
        .expect("Failed to execute command");

    let exit_status = child.wait().expect("Failed to wait on gradle");

    assert!(exit_status.success())
}

#[test]
fn vm_creation_and_object_usage() {
    let mut child = Command::new(
        fs::canonicalize(
            Path::new(".").join("tests").join("driver").join(
                if cfg!(target_os = "windows") { "gradlew.bat" } else { "gradlew" })
        ).expect("Gradle not found"))
        .args(&["test", "-i"])
        .current_dir(
            Path::new(".").join("tests").join("driver").to_str().expect("Failed to get driver path")
        )
        .spawn()
        .expect("Failed to execute command");

    let exit_status = child.wait().expect("Failed to wait on gradle build");
    assert!(exit_status.success());

    let current_dir = std::env::current_dir().expect("Couldn't get current dir");
    let classpath = current_dir.join("tests").join("driver").join("build").join("classes").join("java").join("main");

    // Cargo sets DYLD_FALLBACK_LIBRARY_PATH on os x, but java uses DYLD_LIBRARY_PATH to set java.library.path
    std::env::set_var(
        "DYLD_LIBRARY_PATH",
        format!(
            "{}:{}",
            std::env::var("DYLD_LIBRARY_PATH").unwrap_or("".to_string()),
            std::env::var("DYLD_FALLBACK_LIBRARY_PATH").unwrap_or("".to_string()),
        ));
    let vm_args = InitArgsBuilder::new()
        .option(&*format!(
            "-Djava.class.path={}",
            classpath.to_string_lossy()
        ))
        .build()
        .expect("can't create vm args");
    let vm = JavaVM::new(vm_args).expect("can't create vm");
    let env = vm.attach_current_thread().expect("can't get vm env");

    User::initNative();

    let count = User::getTotalUsersCount(&env)
        .or_else(|e| {
            let _ = print_exception(&env);
            Err(e)
        })
        .expect("can't get user count");

    assert_eq!(count, 0);

    let u = User::new(&env, "user".into(), "password".into()).expect("can't create user instance");

    let count = User::getTotalUsersCount(&env)
        .or_else(|e| {
            let _ = print_exception(&env);
            Err(e)
        })
        .expect("can't get user count");
    assert_eq!(count, 1);

    assert_eq!(
        u.getPassword(&env).expect("can't get user password"),
        "password"
    );

    assert_eq!(
        u.multipleParameters(&env, 10, "test".to_string())
            .expect("Can't test multipleParameters"),
        "test"
    )
}



================================================
FILE: tests/driver/gradlew
================================================
#!/usr/bin/env sh

#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`/" >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo
    echo "$*"
    echo
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
nonstop=false
case "`uname`" in
  CYGWIN* )
    cygwin=true
    ;;
  Darwin* )
    darwin=true
    ;;
  MINGW* )
    msys=true
    ;;
  NONSTOP* )
    nonstop=true
    ;;
esac

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar


# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

# Increase the maximum file descriptors if we can.
if [ "$cygwin" = "false" -a "$darwin" = "false" -a "$nonstop" = "false" ] ; then
    MAX_FD_LIMIT=`ulimit -H -n`
    if [ $? -eq 0 ] ; then
        if [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "max" ] ; then
            MAX_FD="$MAX_FD_LIMIT"
        fi
        ulimit -n $MAX_FD
        if [ $? -ne 0 ] ; then
            warn "Could not set maximum file descriptor limit: $MAX_FD"
        fi
    else
        warn "Could not query maximum file descriptor limit: $MAX_FD_LIMIT"
    fi
fi

# For Darwin, add options to specify how the application appears in the dock
if $darwin; then
    GRADLE_OPTS="$GRADLE_OPTS \"-Xdock:name=$APP_NAME\" \"-Xdock:icon=$APP_HOME/media/gradle.icns\""
fi

# For Cygwin or MSYS, switch paths to Windows format before running java
if [ "$cygwin" = "true" -o "$msys" = "true" ] ; then
    APP_HOME=`cygpath --path --mixed "$APP_HOME"`
    CLASSPATH=`cygpath --path --mixed "$CLASSPATH"`
    
    JAVACMD=`cygpath --unix "$JAVACMD"`

    # We build the pattern for arguments to be converted via cygpath
    ROOTDIRSRAW=`find -L / -maxdepth 1 -mindepth 1 -type d 2>/dev/null`
    SEP=""
    for dir in $ROOTDIRSRAW ; do
        ROOTDIRS="$ROOTDIRS$SEP$dir"
        SEP="|"
    done
    OURCYGPATTERN="(^($ROOTDIRS))"
    # Add a user-defined pattern to the cygpath arguments
    if [ "$GRADLE_CYGPATTERN" != "" ] ; then
        OURCYGPATTERN="$OURCYGPATTERN|($GRADLE_CYGPATTERN)"
    fi
    # Now convert the arguments - kludge to limit ourselves to /bin/sh
    i=0
    for arg in "$@" ; do
        CHECK=`echo "$arg"|egrep -c "$OURCYGPATTERN" -`
        CHECK2=`echo "$arg"|egrep -c "^-"`                                 ### Determine if an option

        if [ $CHECK -ne 0 ] && [ $CHECK2 -eq 0 ] ; then                    ### Added a condition
            eval `echo args$i`=`cygpath --path --ignore --mixed "$arg"`
        else
            eval `echo args$i`="\"$arg\""
        fi
        i=`expr $i + 1`
    done
    case $i in
        0) set -- ;;
        1) set -- "$args0" ;;
        2) set -- "$args0" "$args1" ;;
        3) set -- "$args0" "$args1" "$args2" ;;
        4) set -- "$args0" "$args1" "$args2" "$args3" ;;
        5) set -- "$args0" "$args1" "$args2" "$args3" "$args4" ;;
        6) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" ;;
        7) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" ;;
        8) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" "$args7" ;;
        9) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" "$args7" "$args8" ;;
    esac
fi

# Escape application args
save () {
    for i do printf %s\\n "$i" | sed "s/'/'\\\\''/g;1s/^/'/;\$s/\$/' \\\\/" ; done
    echo " "
}
APP_ARGS=`save "$@"`

# Collect all arguments for the java command, following the shell quoting and substitution rules
eval set -- $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS "\"-Dorg.gradle.appname=$APP_BASE_NAME\"" -classpath "\"$CLASSPATH\"" org.gradle.wrapper.GradleWrapperMain "$APP_ARGS"

exec "$JAVACMD" "$@"



================================================
FILE: tests/driver/gradlew.bat
================================================
@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Gradle startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar


@rem Execute Gradle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable GRADLE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%GRADLE_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega



================================================
FILE: tests/driver/gradle/wrapper/gradle-wrapper.properties
================================================
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-7.3-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists



================================================
FILE: tests/driver/native/Cargo.toml
================================================
[package]
name = "native"
version = "0.2.2"
authors = ["Giovanni Berti <dev.giovanniberti@gmail.com>"]
edition = "2018"

[lib]
crate-type = ["lib", "cdylib"]

[dependencies]
robusta_jni = { path = "../../..", version = "0.2" }
env_logger = "^0"



================================================
FILE: tests/driver/native/src/lib.rs
================================================
use robusta_jni::bridge;

#[bridge]
pub mod jni {
    use std::convert::TryInto;

    use robusta_jni::convert::{
        IntoJavaValue, JValueWrapper, Signature, TryFromJavaValue, TryIntoJavaValue,
    };
    use robusta_jni::jni::errors::Result as JniResult;
    use robusta_jni::jni::objects::AutoLocal;
    use robusta_jni::jni::JNIEnv;

    #[derive(Signature, TryIntoJavaValue, IntoJavaValue, TryFromJavaValue)]
    #[package()]
    pub struct User<'env: 'borrow, 'borrow> {
        #[instance]
        raw: AutoLocal<'env, 'borrow>,
        password: String,
    }

    impl<'env: 'borrow, 'borrow> User<'env, 'borrow> {
        pub extern "jni" fn initNative() {
            std::env::var("RUST_LOG").unwrap_or_else(|_| {
                std::env::set_var("RUST_LOG", "info");
                "info".to_string()
            });
            println!(
                "Initialized env logger with level: {}",
                std::env::var("RUST_LOG").unwrap()
            );
            env_logger::init();
        }

        pub extern "jni" fn userCountStatus(env: &JNIEnv) -> String {
            let users_count: i32 = JValueWrapper::from(
                env.get_static_field("User", "TOTAL_USERS_COUNT", "I")
                    .unwrap(),
            )
            .try_into()
            .unwrap();
            users_count.to_string()
        }

        pub extern "jni" fn hashedPassword(self, _env: &JNIEnv, _seed: i32) -> String {
            let user_pw: String = self.password;
            user_pw + "_pass"
        }

        pub extern "jni" fn getInt(self, v: i32) -> i32 {
            v
        }

        pub extern "jni" fn getBool(self, v: bool) -> bool {
            v
        }

        pub extern "jni" fn getChar(self, v: char) -> char {
            v
        }

        pub extern "jni" fn getByte(self, v: i8) -> i8 {
            v
        }

        pub extern "jni" fn getFloat(self, v: f32) -> f32 {
            v
        }

        pub extern "jni" fn getDouble(self, v: f64) -> f64 {
            v
        }

        pub extern "jni" fn getLong(self, v: i64) -> i64 {
            v
        }

        pub extern "jni" fn getShort(self, v: i16) -> i16 {
            v
        }

        pub extern "jni" fn getString(self, v: String) -> String {
            v
        }

        pub extern "jni" fn getIntArray(self, v: Vec<i32>) -> Vec<i32> {
            v
        }

        pub extern "jni" fn getStringArray(self, v: Vec<String>) -> Vec<String> {
            v
        }

        pub extern "jni" fn getByteArray(self, v: Box<[u8]>) -> Box<[u8]> {
            v
        }

        pub extern "jni" fn intToString(self, v: i32) -> String {
            format!("{}", v)
        }

        pub extern "jni" fn boolToString(self, v: bool) -> String {
            format!("{}", v)
        }

        pub extern "jni" fn charToString(self, v: char) -> String {
            format!("{}", v)
        }

        pub extern "jni" fn byteToString(self, v: i8) -> String {
            format!("{}", v)
        }

        pub extern "jni" fn floatToString(self, v: f32) -> String {
            format!("{}", v)
        }

        pub extern "jni" fn doubleToString(self, v: f64) -> String {
            format!("{}", v)
        }

        pub extern "jni" fn longToString(self, v: i64) -> String {
            format!("{}", v)
        }

        pub extern "jni" fn shortToString(self, v: i16) -> String {
            format!("{}", v)
        }

        pub extern "jni" fn intArrayToString(self, v: Vec<i32>) -> String {
            format!("{:?}", v)
        }

        pub extern "jni" fn stringArrayToString(self, v: Vec<String>) -> String {
            format!("{:?}", v)
        }

        pub extern "jni" fn byteArrayToString(self, v: Box<[u8]>) -> String {
            format!("{:?}", v)
        }

        pub extern "java" fn getPassword(
            &self,
            env: &JNIEnv,
        ) -> ::robusta_jni::jni::errors::Result<String> {
        }

        pub extern "java" fn getTotalUsersCount(
            env: &JNIEnv,
        ) -> ::robusta_jni::jni::errors::Result<i32> {
        }

        pub extern "java" fn multipleParameters(
            &self,
            env: &JNIEnv,
            v: i32,
            s: String,
        ) -> ::robusta_jni::jni::errors::Result<String> {
        }

        #[constructor]
        pub extern "java" fn new(
            env: &'borrow JNIEnv<'env>,
            username: String,
            password: String,
        ) -> JniResult<Self> {
        }
    }
}



================================================
FILE: tests/driver/src/main/java/User.java
================================================
import java.util.List;

public class User {
    static {
        System.loadLibrary("native");
        initNative();
    }

    private static int TOTAL_USERS_COUNT = 0;

    private String username;
    private String password;

    public native int getInt(int x);

    public native boolean getBool(boolean x);

    public native char getChar(char x);

    public native byte getByte(byte x);

    public native float getFloat(float x);

    public native double getDouble(double x);

    public native long getLong(long x);

    public native short getShort(short x);

    public native String getString(String x);

    public native List<Integer> getIntArray(List<Integer> x);

    public native List<String> getStringArray(List<String> x);

    public native byte[] getByteArray(byte[] x);

    public native String intToString(int x);

    public native String boolToString(boolean x);

    public native String charToString(char x);

    public native String byteToString(byte x);

    public native String floatToString(float x);

    public native String doubleToString(double x);

    public native String longToString(long x);

    public native String shortToString(short x);

    public native String intArrayToString(List<Integer> x);

    public native String stringArrayToString(List<String> x);

    public native String byteArrayToString(byte[] x);

    private native static void initNative();

    public native static String userCountStatus();

    public native String hashedPassword(int seed);

    public User(String username, String password) {
        User.TOTAL_USERS_COUNT += 1;

        this.username = username;
        this.password = password;
    }

    public static int getTotalUsersCount() {
        return TOTAL_USERS_COUNT;
    }

    public String getPassword() {
        return password;
    }

    public String multipleParameters(int i, String s) {
        return s;
    }
}



================================================
FILE: tests/driver/src/test/java/UserTest.java
================================================
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class UserTest {
    private User u;

    @BeforeEach
    public void setUp() {
        this.u = new User("user", "pass");
    }

    @Test
    public void selfMethod() {
        String expected = u.getPassword() + "_pass";
        String actual = u.hashedPassword(User.getTotalUsersCount());
        assertEquals(expected, actual);
    }

    @Test
    public void intTest() {
        assertValueRoundTrip(u::getInt, u::intToString, 0, "0");
        assertValueRoundTrip(u::getInt, u::intToString, 1, "1");
        assertValueRoundTrip(u::getInt, u::intToString, -1, "-1");
        assertValueRoundTrip(u::getInt, u::intToString, Integer.MAX_VALUE, "2147483647");
        assertValueRoundTrip(u::getInt, u::intToString, Integer.MIN_VALUE, "-2147483648");
    }

    @Test
    public void boolTest() {
        assertValueRoundTrip(u::getBool, u::boolToString, false, "false");
        assertValueRoundTrip(u::getBool, u::boolToString, true, "true");
    }

    @Test
    public void charTest() {
        assertValueRoundTrip(u::getChar, u::charToString, 'a', "a");
        assertValueRoundTrip(u::getChar, u::charToString, '\n', "\n");
        assertValueRoundTrip(u::getChar, u::charToString, '你', "你");
        assertValueRoundTrip(u::getChar, u::charToString, Character.MIN_VALUE, "\0");
        // note: Character.MAX_VALUE != char::MAX
        assertValueRoundTrip(u::getChar, u::charToString, Character.MAX_VALUE, "\uffff");
    }

    @Test
    public void byteTest() {
        assertValueRoundTrip(u::getByte, u::byteToString, (byte) 0, "0");
        assertValueRoundTrip(u::getByte, u::byteToString, (byte) 1, "1");
        assertValueRoundTrip(u::getByte, u::byteToString, (byte) -1, "-1");
        assertValueRoundTrip(u::getByte, u::byteToString, Byte.MAX_VALUE, "127");
        assertValueRoundTrip(u::getByte, u::byteToString, Byte.MIN_VALUE, "-128");
    }

    @Test
    public void floatTest() {
        assertValueRoundTrip(u::getFloat, u::floatToString, (float) 0.0, "0");
        assertValueRoundTrip(u::getFloat, u::floatToString, (float) 1.23, "1.23");
        assertValueRoundTrip(u::getFloat, u::floatToString, (float) -123.45, "-123.45");
        assertValueRoundTrip(u::getFloat, u::floatToString, Float.MAX_VALUE, "340282350000000000000000000000000000000");
        assertValueRoundTrip(u::getFloat, u::floatToString, Float.MIN_VALUE, "0.000000000000000000000000000000000000000000001");
        assertValueRoundTrip(u::getFloat, u::floatToString, Float.NaN, "NaN");
        assertValueRoundTrip(u::getFloat, u::floatToString, Float.MIN_NORMAL, "0.000000000000000000000000000000000000011754944");
        assertValueRoundTrip(u::getFloat, u::floatToString, Float.POSITIVE_INFINITY, "inf");
        assertValueRoundTrip(u::getFloat, u::floatToString, Float.NEGATIVE_INFINITY, "-inf");
    }

    @Test
    public void doubleTest() {
        assertValueRoundTrip(u::getDouble, u::doubleToString, 0.0, "0");
        assertValueRoundTrip(u::getDouble, u::doubleToString, 1.23, "1.23");
        assertValueRoundTrip(u::getDouble, u::doubleToString, -123.45, "-123.45");
        assertValueRoundTrip(u::getDouble, u::doubleToString, Double.MAX_VALUE,
                "179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000" +
                        "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" +
                        "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" +
                        "000000000000000000000000000000000000");
        assertValueRoundTrip(u::getDouble, u::doubleToString, Double.MIN_VALUE,
                "0.0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" +
                        "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" +
                        "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" +
                        "00000000000000000000000000000000000000000000000000005");
        assertValueRoundTrip(u::getDouble, u::doubleToString, Double.NaN, "NaN");
        assertValueRoundTrip(u::getDouble, u::doubleToString, Double.MIN_NORMAL,
                "0.000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" +
                        "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" +
                        "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" +
                        "00000000000000000000000000000000000000022250738585072014");
        assertValueRoundTrip(u::getDouble, u::doubleToString, Double.POSITIVE_INFINITY, "inf");
        assertValueRoundTrip(u::getDouble, u::doubleToString, Double.NEGATIVE_INFINITY, "-inf");
    }

    @Test
    public void longTest() {
        assertValueRoundTrip(u::getLong, u::longToString, 0L, "0");
        assertValueRoundTrip(u::getLong, u::longToString, 1L, "1");
        assertValueRoundTrip(u::getLong, u::longToString, -1L, "-1");
        assertValueRoundTrip(u::getLong, u::longToString, Long.MAX_VALUE, "9223372036854775807");
        assertValueRoundTrip(u::getLong, u::longToString, Long.MIN_VALUE, "-9223372036854775808");
    }

    @Test
    public void shortTest() {
        assertValueRoundTrip(u::getShort, u::shortToString, (short) 0, "0");
        assertValueRoundTrip(u::getShort, u::shortToString, (short) 1, "1");
        assertValueRoundTrip(u::getShort, u::shortToString, (short) -1, "-1");
        assertValueRoundTrip(u::getShort, u::shortToString, Short.MAX_VALUE, "32767");
        assertValueRoundTrip(u::getShort, u::shortToString, Short.MIN_VALUE, "-32768");
    }

    @Test
    public void stringTest() {
        assertValueRoundTrip(u::getString, Function.identity(), "", "");
        assertValueRoundTrip(u::getString, Function.identity(), "hello!", "hello!");
        assertValueRoundTrip(u::getString, Function.identity(), "a".repeat(10000), "a".repeat(10000));
        assertValueRoundTrip(u::getString, Function.identity(), "\0a\rb\nc\t", "\0a\rb\nc\t");
        assertValueRoundTrip(u::getString, Function.identity(), "아주 좋습니다", "아주 좋습니다");
        // pirate flag https://unicode.org/emoji/charts/emoji-zwj-sequences.html
        assertValueRoundTrip(u::getString, Function.identity(), "️🏴‍☠️", "️🏴‍☠️");
        assertValueRoundTrip(u::getString, Function.identity(), "️️𒅄", "️️𒅄"); // 4 bytes in utf-8
    }

    @Test
    public void intArrayTest() {
        assertValueRoundTrip(u::getIntArray, u::intArrayToString, List.of(), "[]");
        assertValueRoundTrip(u::getIntArray, u::intArrayToString, List.of(1, 2), "[1, 2]");
    }

    @Test
    public void stringArrayTest() {
        assertValueRoundTrip(u::getStringArray, u::stringArrayToString, List.of(), "[]");
        assertValueRoundTrip(u::getStringArray, u::stringArrayToString, List.of("a", "b", "c"), "[\"a\", \"b\", \"c\"]");
    }

    @Test
    public void byteArrayTest() {
        assertArrayValueRoundTrip(u::getByteArray, u::byteArrayToString, new byte[0], "[]");
        assertArrayValueRoundTrip(u::getByteArray, u::byteArrayToString, new byte[] {1, 2, 3}, "[1, 2, 3]");
    }

    @Test
    public void staticMethod() {
        assertEquals(String.valueOf(User.getTotalUsersCount()), User.userCountStatus());
    }

    private <T> void assertValueRoundTrip(Function<T, T> func, Function<T, String> toString, T value, String text) {
        assertEquals(value, func.apply(value));
        assertEquals(text, toString.apply(value));
    }

    private <T> void assertArrayValueRoundTrip(Function<byte[], byte[]> func, Function<byte[], String> toString, byte[] value, String text) {
        assertArrayEquals(value, func.apply(value));
        assertEquals(text, toString.apply(value));
    }
}



================================================
FILE: .github/dependabot.yml
================================================
version: 2
updates:
  - package-ecosystem: cargo
    directory: /
    pull-request-branch-name:
      separator: "-"
    schedule:
      interval: weekly
    commit-message:
      prefix: ''
    labels: []



================================================
FILE: .github/workflows/dependabot-auto-merge.yml
================================================
name: Dependabot auto-merge
on:
  workflow_run:
    workflows: [ "Java setup, build and test" ]
    types:
      - completed

permissions:
  contents: write
  pull-requests: write

jobs:
  dependabot:
    runs-on: ubuntu-latest
    if: ${{ github.actor == 'dependabot[bot]' && github.event.workflow_run.conclusion == 'success' }}
    steps:
      - name: Enable auto-merge for Dependabot PRs
        run: gh pr merge --auto --merge "$PR_URL"
        env:
          PR_URL: ${{github.event.pull_request.html_url}}
          GITHUB_TOKEN: ${{secrets.GITHUB_TOKEN}}


================================================
FILE: .github/workflows/release.yml
================================================
name: Release on crates.io
on:
  push:
    # Pattern matched against refs/tags
    tags:        
      - '*'           # Push events to every tag not containing /
      # Allow manual triggering
  workflow_dispatch:


env:
  CARGO_TERM_COLOR: always

jobs:
  build-and-test:
    uses: ./.github/workflows/setup_and_test.yml
  publish:
    name: Publish
    runs-on: ubuntu-latest
    needs: build-and-test

    steps:
      - name: Checkout sources
        uses: actions/checkout@v4

      - name: Publish robusta-codegen release
        working-directory: ./robusta-codegen
        run: cargo publish --token ${CRATES_TOKEN}
        env:
          CRATES_TOKEN: ${{ secrets.CRATES_TOKEN }}

      - name: Publish robusta_jni release
        run: cargo publish --token ${CRATES_TOKEN}
        env:
          CRATES_TOKEN: ${{ secrets.CRATES_TOKEN }}



================================================
FILE: .github/workflows/setup_and_test.yml
================================================
name: Java setup, build and test

on:
  workflow_call:

jobs:
  desktop-tests:
    runs-on: ${{ matrix.os }}
    strategy:
      matrix:
        os: ["ubuntu-latest", "macos-latest", "windows-latest"]

    steps:
      - name: Setup Java
        uses: actions/setup-java@v3
        with:
          distribution: 'temurin'
          java-version: '17'
          
      - name: Checkout sources
        uses: actions/checkout@v4

      - name: Add $JAVA_HOME to library path
        if: runner.os == 'Linux'
        run:   |
         echo "LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$JAVA_HOME/lib:$JAVA_HOME/lib/server" >> "$GITHUB_ENV"

      - name: Add $JAVA_HOME to library path
        if: runner.os == 'macOS'
        run:   |
         echo "DYLD_FALLBACK_LIBRARY_PATH=$DYLD_FALLBACK_LIBRARY_PATH:$JAVA_HOME/lib:$JAVA_HOME/lib/server" >> "$GITHUB_ENV"

      - name: Add $JAVA_HOME to library path
        if: runner.os == 'Windows'
        run:   |
         Add-Content $env:GITHUB_PATH "$env:JAVA_HOME\bin;$env:JAVA_HOME\bin\server"

#      - name: Debug session
#        uses: mxschmitt/action-tmate@v3

      - name: Build
        run: cargo build --verbose

      - name: Run tests
        run: cargo test --verbose -- --test-threads=1

  android-build-tests:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout sources
        uses: actions/checkout@v4

      - name: Build Android example
        run: cargo build --package robusta-android-example --verbose



================================================
FILE: .github/workflows/test.yml
================================================
name: Launch tests
on: [push, pull_request]

env:
  CARGO_TERM_COLOR: always

jobs:
  tests:
    if: github.event_name != 'pull_request' || github.event.pull_request.head.repo.full_name != github.event.pull_request.base.repo.full_name
    uses: ./.github/workflows/setup_and_test.yml


