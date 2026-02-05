import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    jvm()
    
    js {
        browser()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    
    sourceSets {
        commonMain.dependencies {
            // put your Multiplatform dependencies here
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
        jvmMain {
            resources.srcDir(layout.buildDirectory.dir("rust-lib"))
        }
    }
}

val cargoDir = file("../kwtransport-ffi")

val cleanRustTask = tasks.register("cleanRust") {
    doLast {
        exec {
            workingDir = cargoDir
            commandLine("cargo", "clean")
        }
    }
}

tasks.named("clean") {
    dependsOn(cleanRustTask)
}

val buildRustTask = tasks.register("buildRust") {
    val release = project.hasProperty("rust.release")
    
    doLast {
        val args = mutableListOf("cargo", "build")
        if (release) args.add("--release")
        
        exec {
            workingDir = cargoDir
            commandLine(args)
        }
        
        val osName = System.getProperty("os.name").lowercase()
        val libName = when {
            osName.contains("win") -> "kwtransport_ffi.dll"
            osName.contains("mac") -> "libkwtransport_ffi.dylib"
            else -> "libkwtransport_ffi.so"
        }
        
        val targetType = if (release) "release" else "debug"
        val sourceFile = cargoDir.resolve("target/$targetType/$libName")
        val targetDir = layout.buildDirectory.dir("rust-lib").get().asFile
        targetDir.mkdirs()
        
        if (sourceFile.exists()) {
             sourceFile.copyTo(File(targetDir, libName), overwrite = true)
        } else {
             throw GradleException("Rust build failed to produce $libName at $sourceFile")
        }
    }
}

tasks.named("jvmProcessResources") {
    dependsOn(buildRustTask)
}

tasks.withType<Test> {
    testLogging {
        showStandardStreams = true
        events("passed", "skipped", "failed")
    }
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
