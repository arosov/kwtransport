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
        }
        jvmMain {
            resources.srcDir(layout.buildDirectory.dir("rust-lib"))
        }
    }
}

val buildRustTask = tasks.register("buildRust") {
    doLast {
        exec {
            workingDir("../kwtransport-ffi")
            commandLine("cargo", "build")
        }
        val osName = System.getProperty("os.name").lowercase()
        val libName = when {
            osName.contains("win") -> "kwtransport_ffi.dll"
            osName.contains("mac") -> "libkwtransport_ffi.dylib"
            else -> "libkwtransport_ffi.so"
        }
        val sourceFile = file("../kwtransport-ffi/target/debug/$libName")
        val targetDir = layout.buildDirectory.dir("rust-lib").get().asFile
        targetDir.mkdirs()
        sourceFile.copyTo(File(targetDir, libName), overwrite = true)
    }
}

tasks.named("jvmProcessResources") {
    dependsOn(buildRustTask)
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
