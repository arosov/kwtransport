Directory structure:
└── liurenjie1024-rust-gradle-plugin/
    ├── README.md
    ├── gradlew
    ├── gradlew.bat
    ├── LICENSE
    ├── gradle/
    │   └── wrapper/
    │       └── gradle-wrapper.properties
    ├── plugin/
    │   ├── build.gradle.kts
    │   ├── settings.gradle.kts
    │   └── src/
    │       ├── main/
    │       │   └── kotlin/
    │       │       └── io/
    │       │           └── github/
    │       │               └── liurenjie1024/
    │       │                   └── gradle/
    │       │                       └── rust/
    │       │                           ├── CargoBuildTask.kt
    │       │                           ├── CargoCleanTask.kt
    │       │                           ├── CargoDocTask.kt
    │       │                           ├── CargoExtension.kt
    │       │                           ├── CargoTestTask.kt
    │       │                           └── RustPlugin.kt
    │       └── test/
    │           └── kotlin/
    │               └── io/
    │                   └── github/
    │                       └── liurenjie1024/
    │                           └── gradle/
    │                               └── rust/
    │                                   └── RustPluginTest.kt
    └── sample/
        ├── build.gradle.kts
        ├── Cargo.toml
        ├── settings.gradle.kts
        └── src/
            └── lib.rs

================================================
FILE: README.md
================================================
Rust Gradle Plugin

![CI](https://github.com/liurenjie1024/rust-gradle-plugin/workflows/CI/badge.svg?branch=master)

This plugin help to integrate rust project into gradle projects.

# Usage

In your *project's* build.gradle.kts, apply plugin and add the `cargo` configuration:

```kotlin
plugins {
    id("io.github.liurenjie1024.gradle.rust") version "<latest version>"
}

configure<io.github.liurenjie1024.gradle.rust.CargoExtension> {
    cargoCommand.set("cargo") // This is optional as default cargo command is cargo
}
```

This has already inserted `cargoBuild`, `cargoClean`, `cargoDoc`, `cargoTest` commands into gradle's life cycle tasks, 
so jut run `build` to compile rust project.
```sh
./gradlew build
```

## Configuration


### `cargoBuild`

`cargoBuild` invokes `cargo build` command, and it has been added as a dependency of build task. It can be configured by 
providing extra arguments, which will be passed to `cargo build` command.

```kotlin
tasks.withType(io.github.liurenjie1024.gradle.rust.CargoBuildTask::class.java).configureEach {
    extraArguments = listOf("--release")
}
```

`extraArguments` will be passed to `cargo build` command.

### `cargoClean`

`cargoClean` invokes `cargo clean` command, and it has been added as a dependency of clean task. It can be configured by 
providing extra arguments, which will be passed to `cargo clean` command.

```kotlin
tasks.withType(io.github.liurenjie1024.gradle.rust.CargoCleanTask::class.java).configureEach {
    extraArguments = listOf("--release")
}
```

### `cargoTest`

`cargoTest` invokes `cargo test` command, and it has been added as a dependency of verification task. It can be configured by 
providing extra arguments, which will be passed to `cargo test` command.

```kotlin
tasks.withType(io.github.liurenjie1024.gradle.rust.CargoTestTask::class.java).configureEach {
    extraArguments = listOf("--release")
}
```

### `cargoDoc`

`cargoDoc` invokes `cargo rustdoc` command, and it can be invoked by running `./gradlew cargoDoc`. It can be configured 
by providing extra arguments, which will be passed to `cargo rustdoc` command.
                                                                                                   
 ```kotlin
tasks.withType(io.github.liurenjie1024.gradle.rust.CargoDocTask::class.java).configureEach {
    extraArguments = listOf("--release")
}
```





================================================
FILE: gradlew
================================================
#!/usr/bin/env sh

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
DEFAULT_JVM_OPTS='"-Xmx64m"'

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

# For Cygwin, switch paths to Windows format before running java
if $cygwin ; then
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
        i=$((i+1))
    done
    case $i in
        (0) set -- ;;
        (1) set -- "$args0" ;;
        (2) set -- "$args0" "$args1" ;;
        (3) set -- "$args0" "$args1" "$args2" ;;
        (4) set -- "$args0" "$args1" "$args2" "$args3" ;;
        (5) set -- "$args0" "$args1" "$args2" "$args3" "$args4" ;;
        (6) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" ;;
        (7) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" ;;
        (8) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" "$args7" ;;
        (9) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" "$args7" "$args8" ;;
    esac
fi

# Escape application args
save () {
    for i do printf %s\\n "$i" | sed "s/'/'\\\\''/g;1s/^/'/;\$s/\$/' \\\\/" ; done
    echo " "
}
APP_ARGS=$(save "$@")

# Collect all arguments for the java command, following the shell quoting and substitution rules
eval set -- $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS "\"-Dorg.gradle.appname=$APP_BASE_NAME\"" -classpath "\"$CLASSPATH\"" org.gradle.wrapper.GradleWrapperMain "$APP_ARGS"

# by default we should be in the correct project dir, but when run from Finder on Mac, the cwd is wrong
if [ "$(uname)" = "Darwin" ] && [ "$HOME" = "$PWD" ]; then
  cd "$(dirname "$0")"
fi

exec "$JAVACMD" "$@"



================================================
FILE: gradlew.bat
================================================
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

@rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Xmx64m"

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
FILE: LICENSE
================================================
                                 Apache License
                           Version 2.0, January 2004
                        http://www.apache.org/licenses/

   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION

   1. Definitions.

      "License" shall mean the terms and conditions for use, reproduction,
      and distribution as defined by Sections 1 through 9 of this document.

      "Licensor" shall mean the copyright owner or entity authorized by
      the copyright owner that is granting the License.

      "Legal Entity" shall mean the union of the acting entity and all
      other entities that control, are controlled by, or are under common
      control with that entity. For the purposes of this definition,
      "control" means (i) the power, direct or indirect, to cause the
      direction or management of such entity, whether by contract or
      otherwise, or (ii) ownership of fifty percent (50%) or more of the
      outstanding shares, or (iii) beneficial ownership of such entity.

      "You" (or "Your") shall mean an individual or Legal Entity
      exercising permissions granted by this License.

      "Source" form shall mean the preferred form for making modifications,
      including but not limited to software source code, documentation
      source, and configuration files.

      "Object" form shall mean any form resulting from mechanical
      transformation or translation of a Source form, including but
      not limited to compiled object code, generated documentation,
      and conversions to other media types.

      "Work" shall mean the work of authorship, whether in Source or
      Object form, made available under the License, as indicated by a
      copyright notice that is included in or attached to the work
      (an example is provided in the Appendix below).

      "Derivative Works" shall mean any work, whether in Source or Object
      form, that is based on (or derived from) the Work and for which the
      editorial revisions, annotations, elaborations, or other modifications
      represent, as a whole, an original work of authorship. For the purposes
      of this License, Derivative Works shall not include works that remain
      separable from, or merely link (or bind by name) to the interfaces of,
      the Work and Derivative Works thereof.

      "Contribution" shall mean any work of authorship, including
      the original version of the Work and any modifications or additions
      to that Work or Derivative Works thereof, that is intentionally
      submitted to Licensor for inclusion in the Work by the copyright owner
      or by an individual or Legal Entity authorized to submit on behalf of
      the copyright owner. For the purposes of this definition, "submitted"
      means any form of electronic, verbal, or written communication sent
      to the Licensor or its representatives, including but not limited to
      communication on electronic mailing lists, source code control systems,
      and issue tracking systems that are managed by, or on behalf of, the
      Licensor for the purpose of discussing and improving the Work, but
      excluding communication that is conspicuously marked or otherwise
      designated in writing by the copyright owner as "Not a Contribution."

      "Contributor" shall mean Licensor and any individual or Legal Entity
      on behalf of whom a Contribution has been received by Licensor and
      subsequently incorporated within the Work.

   2. Grant of Copyright License. Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      copyright license to reproduce, prepare Derivative Works of,
      publicly display, publicly perform, sublicense, and distribute the
      Work and such Derivative Works in Source or Object form.

   3. Grant of Patent License. Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      (except as stated in this section) patent license to make, have made,
      use, offer to sell, sell, import, and otherwise transfer the Work,
      where such license applies only to those patent claims licensable
      by such Contributor that are necessarily infringed by their
      Contribution(s) alone or by combination of their Contribution(s)
      with the Work to which such Contribution(s) was submitted. If You
      institute patent litigation against any entity (including a
      cross-claim or counterclaim in a lawsuit) alleging that the Work
      or a Contribution incorporated within the Work constitutes direct
      or contributory patent infringement, then any patent licenses
      granted to You under this License for that Work shall terminate
      as of the date such litigation is filed.

   4. Redistribution. You may reproduce and distribute copies of the
      Work or Derivative Works thereof in any medium, with or without
      modifications, and in Source or Object form, provided that You
      meet the following conditions:

      (a) You must give any other recipients of the Work or
          Derivative Works a copy of this License; and

      (b) You must cause any modified files to carry prominent notices
          stating that You changed the files; and

      (c) You must retain, in the Source form of any Derivative Works
          that You distribute, all copyright, patent, trademark, and
          attribution notices from the Source form of the Work,
          excluding those notices that do not pertain to any part of
          the Derivative Works; and

      (d) If the Work includes a "NOTICE" text file as part of its
          distribution, then any Derivative Works that You distribute must
          include a readable copy of the attribution notices contained
          within such NOTICE file, excluding those notices that do not
          pertain to any part of the Derivative Works, in at least one
          of the following places: within a NOTICE text file distributed
          as part of the Derivative Works; within the Source form or
          documentation, if provided along with the Derivative Works; or,
          within a display generated by the Derivative Works, if and
          wherever such third-party notices normally appear. The contents
          of the NOTICE file are for informational purposes only and
          do not modify the License. You may add Your own attribution
          notices within Derivative Works that You distribute, alongside
          or as an addendum to the NOTICE text from the Work, provided
          that such additional attribution notices cannot be construed
          as modifying the License.

      You may add Your own copyright statement to Your modifications and
      may provide additional or different license terms and conditions
      for use, reproduction, or distribution of Your modifications, or
      for any such Derivative Works as a whole, provided Your use,
      reproduction, and distribution of the Work otherwise complies with
      the conditions stated in this License.

   5. Submission of Contributions. Unless You explicitly state otherwise,
      any Contribution intentionally submitted for inclusion in the Work
      by You to the Licensor shall be under the terms and conditions of
      this License, without any additional terms or conditions.
      Notwithstanding the above, nothing herein shall supersede or modify
      the terms of any separate license agreement you may have executed
      with Licensor regarding such Contributions.

   6. Trademarks. This License does not grant permission to use the trade
      names, trademarks, service marks, or product names of the Licensor,
      except as required for reasonable and customary use in describing the
      origin of the Work and reproducing the content of the NOTICE file.

   7. Disclaimer of Warranty. Unless required by applicable law or
      agreed to in writing, Licensor provides the Work (and each
      Contributor provides its Contributions) on an "AS IS" BASIS,
      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
      implied, including, without limitation, any warranties or conditions
      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A
      PARTICULAR PURPOSE. You are solely responsible for determining the
      appropriateness of using or redistributing the Work and assume any
      risks associated with Your exercise of permissions under this License.

   8. Limitation of Liability. In no event and under no legal theory,
      whether in tort (including negligence), contract, or otherwise,
      unless required by applicable law (such as deliberate and grossly
      negligent acts) or agreed to in writing, shall any Contributor be
      liable to You for damages, including any direct, indirect, special,
      incidental, or consequential damages of any character arising as a
      result of this License or out of the use or inability to use the
      Work (including but not limited to damages for loss of goodwill,
      work stoppage, computer failure or malfunction, or any and all
      other commercial damages or losses), even if such Contributor
      has been advised of the possibility of such damages.

   9. Accepting Warranty or Additional Liability. While redistributing
      the Work or Derivative Works thereof, You may choose to offer,
      and charge a fee for, acceptance of support, warranty, indemnity,
      or other liability obligations and/or rights consistent with this
      License. However, in accepting such obligations, You may act only
      on Your own behalf and on Your sole responsibility, not on behalf
      of any other Contributor, and only if You agree to indemnify,
      defend, and hold each Contributor harmless for any liability
      incurred by, or claims asserted against, such Contributor by reason
      of your accepting any such warranty or additional liability.

   END OF TERMS AND CONDITIONS

   APPENDIX: How to apply the Apache License to your work.

      To apply the Apache License to your work, attach the following
      boilerplate notice, with the fields enclosed by brackets "[]"
      replaced with your own identifying information. (Don't include
      the brackets!)  The text should be enclosed in the appropriate
      comment syntax for the file format. We also recommend that a
      file or class name and description of purpose be included on the
      same "printed page" as the copyright notice for easier
      identification within third-party archives.

   Copyright 2018 Nish Tahir

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.



================================================
FILE: gradle/wrapper/gradle-wrapper.properties
================================================
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-6.6.1-all.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists



================================================
FILE: plugin/build.gradle.kts
================================================
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
plugins {
    `java-gradle-plugin`
    kotlin("jvm") version "1.4.10"
    id("com.gradle.plugin-publish") version "0.12.0"
}

repositories {
    mavenCentral()
}

group = "io.github.liurenjie1024.gradle.rust"
version = "0.2.0-SNAPSHOT"

gradlePlugin {
    plugins {
        create("rustPlugin") {
            id = "io.github.liurenjie1024.gradle.rust"
            displayName = "Rust Plugin"
            description = "Gradle plugin to help integrate rust project into gralde project"
            implementationClass = "io.github.liurenjie1024.gradle.rust.RustPlugin"
        }
    }
}

dependencies {
    implementation(kotlin("test-junit"))
    testImplementation("junit:junit:4.12")
}



tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

pluginBundle {
    website = "https://github.com/liurenjie1024/rust-gradle-plugin"
    vcsUrl = "https://github.com/liurenjie1024/rust-gradle-plugin"
    tags = listOf("rust", "rust-lang")
}



================================================
FILE: plugin/settings.gradle.kts
================================================
rootProject.name = "rust-gradle-plugin"



================================================
FILE: plugin/src/main/kotlin/io/github/liurenjie1024/gradle/rust/CargoBuildTask.kt
================================================
package io.github.liurenjie1024.gradle.rust;

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction


open class CargoBuildTask: DefaultTask() {
    companion object {
        const val NAME = "cargoBuild"
    }
    @Input
    val cargoCommand: Property<String> = project.objects.property(String::class.java)
    @Input
    var verbose: Boolean = false
    @Input
    var release: Boolean = false
    @Input
    var extraArguments: List<String> = emptyList()
    @Input
    var featureSpec: FeatureSpec = FeatureSpec.defaultAnd()

    @Suppress("unused")
    @TaskAction
    fun build() {
        project.exec {
            it.commandLine = buildCommandLine()
        }.assertNormalExitValue()
    }

    fun buildCommandLine(): List<String> {
        val commandLine = mutableListOf(cargoCommand.get(), "build")

        if (verbose) {
            commandLine += "--verbose"
        }

        // We just pass this along to cargo as something space separated... AFAICT
        // you're allowed to have featureSpec with spaces in them, but I don't think
        // there's a way to specify them in the cargo command line -- rustc accepts
        // them if passed in directly with `--cfg`, and cargo will pass them to rustc
        // if you use them as default featureSpec.
        when (featureSpec.type) {
            FeaturesType.All -> {
                commandLine += "--all-features"
            }
            FeaturesType.Default -> {
                if (!featureSpec.featureSet.isEmpty()) {
                    commandLine += "--features"
                    commandLine += featureSpec.featureSet.joinToString(" ")
                }
            }
            FeaturesType.NoDefault -> {
                commandLine += "--no-default-features"
                if (!featureSpec.featureSet.isEmpty()) {
                    commandLine += "--features"
                    commandLine += featureSpec.featureSet.joinToString(" ")
                }
            }
        }

        if (release) {
            // Cargo is rigid: it accepts "--release" for release (and
            // nothing for dev).  This is a cheap way of allowing only
            // two values.
            commandLine += "--release"
        }

        extraArguments?.let {
            commandLine.addAll(it)
        }

        return commandLine
    }
}




================================================
FILE: plugin/src/main/kotlin/io/github/liurenjie1024/gradle/rust/CargoCleanTask.kt
================================================
package io.github.liurenjie1024.gradle.rust

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class CargoCleanTask: DefaultTask() {
    companion object {
        const val NAME = "cargoClean"
    }

    @Input
    val cargoCommand: Property<String> = project.objects.property(String::class.java)
    @Input
    var verbose: Boolean = false
    @Input
    var extraArguments: List<String> = emptyList()

    @Suppress("unused")
    @TaskAction
    fun clean() {
        project.exec {
            it.commandLine = buildCommandLine()
        }.assertNormalExitValue()
    }

    fun buildCommandLine(): List<String> {
        val commandLine = mutableListOf(cargoCommand.get(), "clean")

        if (verbose) {
            commandLine += "--verbose"
        }

        extraArguments?.let {
            commandLine.addAll(it)
        }

        return commandLine
    }
}


================================================
FILE: plugin/src/main/kotlin/io/github/liurenjie1024/gradle/rust/CargoDocTask.kt
================================================
package io.github.liurenjie1024.gradle.rust

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class CargoDocTask: DefaultTask() {
    companion object {
        const val NAME = "cargoDoc"
    }

    @Input
    val cargoCommand: Property<String> = project.objects.property(String::class.java)
    @Input
    var extraCargoBuildArguments: List<String> = emptyList()

    @Suppress("unused")
    @TaskAction
    fun clean() {
        project.exec {
            it.commandLine = buildCommandLine()
        }.assertNormalExitValue()
    }

    fun buildCommandLine(): List<String> {
        val commandLine = mutableListOf(cargoCommand.get(), "rustdoc")

        extraCargoBuildArguments?.let {
            commandLine.addAll(it)
        }

        return commandLine
    }
}


================================================
FILE: plugin/src/main/kotlin/io/github/liurenjie1024/gradle/rust/CargoExtension.kt
================================================
package io.github.liurenjie1024.gradle.rust

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property


enum class FeaturesType { All, Default, NoDefault}

data class FeatureSpec private constructor(val type: FeaturesType = FeaturesType.Default, val featureSet: Set<String> = emptySet()) {
    companion object {
        fun all(): FeatureSpec = FeatureSpec(type = FeaturesType.All)

        fun defaultAnd(extraFeatures: Array<String> = emptyArray()) = FeatureSpec(featureSet = extraFeatures.toSet())

        fun noDefaultBut(features: Array<String>) = FeatureSpec(type = FeaturesType.NoDefault, featureSet = features.toSet())
    }
}

open class CargoExtension(objects: ObjectFactory) {
    companion object {
        const val NAME = "cargo"
    }
    val cargoCommand: Property<String> = objects.property(String::class.java).value(DEFAULT_CARGO_COMMAND)
}



================================================
FILE: plugin/src/main/kotlin/io/github/liurenjie1024/gradle/rust/CargoTestTask.kt
================================================
package io.github.liurenjie1024.gradle.rust

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class CargoTestTask: DefaultTask() {
    companion object {
        const val NAME = "cargoTest"
    }

    @Input
    val cargoCommand: Property<String> = project.objects.property(String::class.java)
    @Input
    var extraArguments: List<String> = emptyList()

    @Suppress("unused")
    @TaskAction
    fun clean() {
        project.exec {
            it.commandLine = buildCommandLine()
        }.assertNormalExitValue()
    }

    fun buildCommandLine(): List<String> {
        val commandLine = mutableListOf(cargoCommand.get(), "test")

        extraArguments.let {
            commandLine.addAll(it)
        }

        return commandLine
    }
}


================================================
FILE: plugin/src/main/kotlin/io/github/liurenjie1024/gradle/rust/RustPlugin.kt
================================================
package io.github.liurenjie1024.gradle.rust

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin

const val DEFAULT_CARGO_COMMAND: String = "cargo"

open class RustPlugin : Plugin<Project> {
    private lateinit var cargoExtension: CargoExtension

    override fun apply(project: Project) {
        with(project) {
            // Apply base plugin
            project.pluginManager.apply(BasePlugin::class.java)
            cargoExtension = extensions.create(CargoExtension.NAME, CargoExtension::class.java)

            createCargoBuildTask(project)
            createCargoCleanTask(project)
            createCargoTestTask(project)
            createCargoDocTask(project)
        }
    }

    private fun createCargoBuildTask(project: Project) {
        with(project) {
            // Create cargo build task
            val cargoBuildTask = tasks.create(CargoBuildTask.NAME, CargoBuildTask::class.java).apply {
                group = "build"
                description = "Run cargo build command"
                cargoCommand.set(cargoExtension.cargoCommand)
            }
            tasks.getByPath("build").dependsOn(cargoBuildTask)
        }
    }

    private fun createCargoCleanTask(project: Project) {
        with(project) {
            // Create cargo build task
            val cargoCleanTask = tasks.create(CargoCleanTask.NAME, CargoCleanTask::class.java).apply {
                group = "build"
                description = "Run cargo clean command"
                cargoCommand.set(cargoExtension.cargoCommand)
            }
            tasks.getByPath("clean").dependsOn(cargoCleanTask)
        }
    }

    private fun createCargoTestTask(project: Project) {
        with(project) {
            // Create cargo build task
            val cargoTestTask = tasks.create(CargoTestTask.NAME, CargoTestTask::class.java).apply {
                group = "verification"
                description = "Run cargo test command"
                cargoCommand.set(cargoExtension.cargoCommand)
            }
            tasks.getByPath("check").dependsOn(cargoTestTask)
        }
    }

    private fun createCargoDocTask(project: Project) {
        with(project) {
            // Create cargo build task
            val cargoTestTask = tasks.create(CargoDocTask.NAME, CargoDocTask::class.java).apply {
                group = "documentation"
                description = "Run cargo rustdoc command"
                cargoCommand.set(cargoExtension.cargoCommand)
            }
        }
    }
}


================================================
FILE: plugin/src/test/kotlin/io/github/liurenjie1024/gradle/rust/RustPluginTest.kt
================================================
package io.github.liurenjie1024.gradle.rust

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

const val PLUGIN_ID: String = "io.github.liurenjie1024.gradle.rust"

class RustPluginTest {
    private lateinit var project: Project

    @Before
    fun setupTest() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply(PLUGIN_ID)

        project.extensions.configure(CargoExtension::class.java) {
            it.cargoCommand.set("cargo2")
        }
    }

    @Test
    fun checkCargoBuildTask() {
        val task = project.tasks.getByName(CargoBuildTask.NAME)
        assertTrue(task is CargoBuildTask)

        task.apply {
            verbose = true
            release = true
            extraArguments = listOf("extra")
            featureSpec = FeatureSpec.all()
        }

        assertEquals(listOf("cargo2", "build", "--verbose", "--all-features", "--release", "extra"), task.buildCommandLine())
    }

    @Test
    fun checkCargoCleanTask() {
        val task = project.tasks.getByName(CargoCleanTask.NAME)
        assertTrue(task is CargoCleanTask)

        task.apply {
            verbose = true
            extraArguments = listOf("extra")
        }

        assertEquals(listOf("cargo2", "clean", "--verbose", "extra"), task.buildCommandLine())
    }

    @Test
    fun checkCargoTestTask() {
        val task = project.tasks.getByName(CargoTestTask.NAME)
        assertTrue(task is CargoTestTask)

        task.apply {
            extraArguments = listOf("extra")
        }

        assertEquals(listOf("cargo2", "test", "extra"), task.buildCommandLine())
    }

    @Test
    fun checkCargoDocTask() {
        val task = project.tasks.getByName(CargoDocTask.NAME)
        assertTrue(task is CargoDocTask)

        task.apply {
            extraCargoBuildArguments = listOf("extra")
        }

        assertEquals(listOf("cargo2", "rustdoc", "extra"), task.buildCommandLine())
    }
}


================================================
FILE: sample/build.gradle.kts
================================================
import io.github.liurenjie1024.gradle.rust.FeatureSpec as CargoFeatureSpec
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("io.github.liurenjie1024.gradle.rust:rust-gradle-plugin:0.1.0")
    }
}

apply(plugin = "io.github.liurenjie1024.gradle.rust")

configure<io.github.liurenjie1024.gradle.rust.CargoExtension> {
    cargoCommand.set("cargo")
}

tasks.withType(io.github.liurenjie1024.gradle.rust.CargoBuildTask::class.java).configureEach {
    verbose = false
    release = true
    featureSpec = CargoFeatureSpec.all()
}

tasks.withType(io.github.liurenjie1024.gradle.rust.CargoCleanTask::class.java).configureEach {
    verbose = false
}




================================================
FILE: sample/Cargo.toml
================================================
[package]
name = "sample"
version = "0.1.0"
authors = ["Renjie Liu <liurenjie2008@gmail.com>"]
edition = "2018"

# See more keys and their definitions at https://doc.rust-lang.org/cargo/reference/manifest.html

[dependencies]



================================================
FILE: sample/settings.gradle.kts
================================================
rootProject.name = "sample"
includeBuild("../plugin")


================================================
FILE: sample/src/lib.rs
================================================
#[cfg(test)]
mod tests {
    #[test]
    fn it_works() {
        assert_eq!(2 + 2, 4);
    }
}


