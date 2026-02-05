import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.vanniktech.maven.publish.AndroidMultiVariantLibrary
import java.util.concurrent.TimeUnit
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.publish)
    alias(libs.plugins.cargo.ndk.android)
    signing
}

val isCi = System.getenv("CI") == "true"

signing {
    if (!isCi) {
        useGpgCmd()
    }
}

// Android Rust build configuration
cargoNdk {
    module = "kwtransport-ffi"
    targets = arrayListOf("arm64", "arm", "x86", "x86_64")
}

// Load local.properties into project properties so the publishing plugin can see them
if (file("../local.properties").exists()) {
    val localProperties = Properties()
    file("../local.properties").inputStream().use { localProperties.load(it) }
    localProperties.forEach { key, value ->
        val k = key.toString()
        project.extensions.extraProperties.set(k, value)
        // Map to GPG specific properties if needed
        if (k == "signing.keyId") project.extensions.extraProperties.set("signing.gnupg.keyName", value)
        if (k == "signing.password") project.extensions.extraProperties.set("signing.gnupg.passphrase", value)
    }
}

// Override version from environment variable if provided
val envVersion = System.getenv("VERSION")
val finalVersion = if (!envVersion.isNullOrBlank()) {
    val versionRegex = """^\d+\.\d+\.\d+(-SNAPSHOT)?$""".toRegex()
    if (!versionRegex.matches(envVersion)) {
        throw GradleException("Invalid VERSION environment variable: '$envVersion'. Expected x.y.z or x.y.z-SNAPSHOT")
    }
    envVersion
} else {
    project.findProperty("VERSION_NAME")?.toString() ?: "0.0.0"
}

project.version = finalVersion
project.extensions.extraProperties.set("VERSION_NAME", finalVersion)

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    // Configure Android variant to publish (Debug only to skip release signing requirements)
    configure(com.vanniktech.maven.publish.AndroidSingleVariantLibrary(
        variant = "debug"
    ))

    pom {
        name.set("kwtransport")
        description.set("High-Performance WebTransport for Kotlin Multiplatform")
        url.set("https://github.com/arosov/kwtransport")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("arosov")
                name.set("Alexis Rosovsky")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/arosov/kwtransport.git")
            developerConnection.set("scm:git:ssh://github.com/arosov/kwtransport.git")
            url.set("https://github.com/arosov/kwtransport")
        }
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    jvm {
        compilerOptions {
            // JVM doesn't need these opt-ins, they are WASM-specific
        }
    }
    
    js {
        browser()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
        compilerOptions {
            freeCompilerArgs.add("-opt-in=kotlin.js.ExperimentalWasmJsInterop")
            freeCompilerArgs.add("-opt-in=kotlin.js.ExperimentalJsExport")
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(project(":libraries:test-support"))
        }
        
        jvmTest.dependencies {
            // For testing, we depend on the native artifact of the current host
            val platform = getCurrentPlatform()
            if (platform != "unknown") {
                implementation(project(":native:$platform"))
            }
            implementation(project(":libraries:test-support"))
        }

        val jvmAndroidMain by creating {
            dependsOn(commonMain.get())
        }
        jvmMain.get().dependsOn(jvmAndroidMain)
        androidMain.get().dependsOn(jvmAndroidMain)
    }
}

// Function to detect current platform for Rust build
fun getCurrentPlatform(): String {
    val os = System.getProperty("os.name").lowercase()
    val arch = System.getProperty("os.arch").lowercase()
    return when {
        os.contains("linux") -> if (arch.contains("aarch64") || arch.contains("arm64")) "linux-arm64" else "linux-x64"
        os.contains("mac") -> if (arch.contains("aarch64") || arch.contains("arm64")) "macos-arm64" else "macos-x64"
        os.contains("win") -> "windows-x64"
        else -> "unknown"
    }
}

tasks.register("printPlatform") {
    val os = System.getProperty("os.name").lowercase()
    val arch = System.getProperty("os.arch").lowercase()
    val platform = when {
        os.contains("linux") -> if (arch.contains("aarch64") || arch.contains("arm64")) "linux-arm64" else "linux-x64"
        os.contains("mac") -> if (arch.contains("aarch64") || arch.contains("arm64")) "macos-arm64" else "macos-x64"
        os.contains("win") -> "windows-x64"
        else -> "unknown"
    }
    doLast {
        println(platform)
    }
}

val cargoBuild = tasks.register<Exec>("cargoBuild") {
    workingDir = file("../kwtransport-ffi")
    val isRelease = project.hasProperty("rust.release")
    val cmd = mutableListOf("cargo", "build")
    if (isRelease) cmd.add("--release")
    commandLine(cmd)
}

val buildRustTask = tasks.register("buildRust") {
    dependsOn(cargoBuild)
    val isRelease = project.hasProperty("rust.release")
    val platform = getCurrentPlatform()
    val cargoDir = layout.projectDirectory.dir("../kwtransport-ffi").asFile
    val targetDir = file("../native/$platform/src/main/resources/native/$platform")

    doLast {
        val osName = System.getProperty("os.name").lowercase()
        val libName = when {
            osName.contains("win") -> "kwtransport_ffi.dll"
            osName.contains("mac") -> "libkwtransport_ffi.dylib"
            else -> "libkwtransport_ffi.so"
        }
        
        val targetType = if (isRelease) "release" else "debug"
        val sourceFile = cargoDir.resolve("target/$targetType/$libName")
        targetDir.mkdirs()
        
        if (sourceFile.exists()) {
             sourceFile.copyTo(File(targetDir, libName), overwrite = true)
        } else {
             throw GradleException("Rust build failed to produce $libName at $sourceFile")
        }
    }
}

val cleanRustTask = tasks.register<Exec>("cleanRust") {
    workingDir = file("../kwtransport-ffi")
    commandLine("cargo", "clean")
}

tasks.named("clean") {
    dependsOn(cleanRustTask)
}

tasks.named("jvmProcessResources") {
    dependsOn(buildRustTask)
}

tasks.withType<Test> {
    testLogging {
        showStandardStreams = true
        events("passed", "skipped", "failed")
    }
    
    filter {
        val excludePattern = project.findProperty("excludeTests") as? String
        if (excludePattern != null) {
            excludeTestsMatching(excludePattern)
        }
        
        // Exclude stress test by default, but allow enabling via -PincludeStressTests
        if (!project.hasProperty("includeStressTests")) {
            excludeTestsMatching("io.github.arosov.kwtransport.FfiStressTest")
        }
    }
}

// Manual server classpath printer for convenience
tasks.register("printTestClasspath") {
    dependsOn("jvmJar", "jvmTestProcessResources", "compileTestKotlinJvm")
    
    val jvmTestRuntimeClasspathFiles = configurations.getByName("jvmTestRuntimeClasspath").incoming.files
    val compileKotlinJvmTask = tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlinJvm")
    val compileTestKotlinJvmTask = tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileTestKotlinJvm")
    val jvmTestProcessResourcesTask = tasks.named("jvmTestProcessResources")

    val testClassesDir = compileTestKotlinJvmTask.flatMap { it.destinationDirectory }
    val mainClassesDir = compileKotlinJvmTask.flatMap { it.destinationDirectory }
    val testResourcesFiles = jvmTestProcessResourcesTask.map { it.outputs.files }
    
    val classpathFiles = objects.fileCollection().from(
        jvmTestRuntimeClasspathFiles,
        mainClassesDir,
        testClassesDir,
        testResourcesFiles
    )

    doLast {
        println("CLASSPATH=" + classpathFiles.asPath)
    }
}

tasks.named("wasmJsTest") {
}

tasks.named("jsTest") {
}

android {
    namespace = "io.github.arosov.kwtransport"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}