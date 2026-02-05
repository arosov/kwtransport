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

tasks.register<JavaExec>("runTestServer") {
    group = "verification"
    mainClass.set("ovh.devcraft.kwtransport.TestEchoServerKt")
    classpath = sourceSets["main"].runtimeClasspath
    
    // Set library path to the shared module's rust-lib build directory
    val sharedProject = project(":shared")
    val rustLibDir = sharedProject.layout.buildDirectory.dir("rust-lib")
    systemProperty("java.library.path", rustLibDir.get().asFile.absolutePath)
    
    dependsOn(":shared:buildRust")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}