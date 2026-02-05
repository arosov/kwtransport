import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.concurrent.TimeUnit

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
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
        }
        val jvmAndroidMain by creating {
            dependsOn(commonMain.get())
            resources.srcDir(layout.buildDirectory.dir("rust-lib"))
        }
        jvmMain.get().dependsOn(jvmAndroidMain)
        androidMain.get().dependsOn(jvmAndroidMain)
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
    val cargoDir = layout.projectDirectory.dir("../kwtransport-ffi").asFile
    val targetDir = layout.buildDirectory.dir("rust-lib").get().asFile

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
            excludeTestsMatching("ovh.devcraft.kwtransport.FfiStressTest")
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
    namespace = "ovh.devcraft.kwtransport.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}