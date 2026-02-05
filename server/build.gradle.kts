plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "ovh.devcraft.kwtransport"
version = "1.0.0"
application {
    mainClass.set("ovh.devcraft.kwtransport.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

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

tasks.register<JavaExec>("runTestServer") {
    group = "verification"
    mainClass.set("ovh.devcraft.kwtransport.TestEchoServerKt")
    classpath = sourceSets["main"].runtimeClasspath
    
    dependsOn(":shared:buildRust")
}

dependencies {
    implementation(projects.shared)
    implementation(project(":libraries:test-support"))
    
    // Depend on the native artifact of the current host
    val platform = getCurrentPlatform()
    if (platform != "unknown") {
        implementation(project(":native:$platform"))
    }

    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}