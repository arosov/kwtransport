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
    alias(libs.plugins.dokka)
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

// Override version from environment variable or project property if provided
val envVersion = System.getenv("VERSION") ?: project.findProperty("VERSION_NAME")?.toString()
val finalVersion = if (!envVersion.isNullOrBlank() && envVersion != "0.0.0") {
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

    // Automatic configuration should handle Dokka and variants when both plugins are applied.
    // Explicitly setting them via configure() often causes property finality conflicts in KMP.

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
        withXml {
            val profilesNode = asNode().appendNode("profiles")
            val group = "io.github.arosov"
            val version = project.version.toString()
            
            fun addProfile(id: String, osFamily: String, osArch: String, artifactId: String) {
                val profile = profilesNode.appendNode("profile")
                profile.appendNode("id", id)
                val activation = profile.appendNode("activation")
                val os = activation.appendNode("os")
                os.appendNode("family", osFamily)
                if (osArch.isNotEmpty()) {
                    os.appendNode("arch", osArch)
                }
                
                val dependenciesNode = profile.appendNode("dependencies")
                val dependency = dependenciesNode.appendNode("dependency")
                dependency.appendNode("groupId", group)
                dependency.appendNode("artifactId", artifactId)
                dependency.appendNode("version", version)
                dependency.appendNode("scope", "runtime")
            }

            addProfile("linux-x64", "linux", "amd64", "kwtransport-jvm-linux-x64")
            addProfile("linux-arm64", "linux", "aarch64", "kwtransport-jvm-linux-arm64")
            addProfile("macos-x64", "mac", "x86_64", "kwtransport-jvm-macos-x64")
            addProfile("macos-arm64", "mac", "aarch64", "kwtransport-jvm-macos-arm64")
            addProfile("windows-x64", "windows", "amd64", "kwtransport-jvm-windows-x64")
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
        
        jvmMain.dependencies {
            // Native dependencies are now handled via Maven profiles in the POM 
            // and Gradle variant-aware resolution to avoid downloading all binaries.
        }

        jvmTest.dependencies {
            implementation(project(":libraries:test-support"))
            // Include supported native platforms for local testing.
            runtimeOnly(project(":native:linux-x64"))
            runtimeOnly(project(":native:macos-arm64"))
            runtimeOnly(project(":native:windows-x64"))
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
    // Use the base native resources directory
    val targetDir = file("../native/$platform/src/main/resources/native")

    doLast {
        val osName = System.getProperty("os.name").lowercase()
        val extension = when {
            osName.contains("win") -> "dll"
            osName.contains("mac") -> "dylib"
            else -> "so"
        }
        val sourceLibName = if (extension == "dll") "kwtransport_ffi.dll" else "libkwtransport_ffi.$extension"
        val targetLibName = if (extension == "dll") "kwtransport_ffi-$platform.dll" else "libkwtransport_ffi-$platform.$extension"
        
        val targetType = if (isRelease) "release" else "debug"
        val sourceFile = cargoDir.resolve("target/$targetType/$sourceLibName")
        targetDir.mkdirs()
        
        if (sourceFile.exists()) {
             sourceFile.copyTo(File(targetDir, targetLibName), overwrite = true)
        } else {
             throw GradleException("Rust build failed to produce $sourceLibName at $sourceFile")
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