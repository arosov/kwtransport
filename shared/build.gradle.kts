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
    
    val excludePattern = project.findProperty("excludeTests") as? String
    if (excludePattern != null) {
        filter {
             excludeTestsMatching(excludePattern)
        }
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
