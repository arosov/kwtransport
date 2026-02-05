plugins {
    kotlin("jvm")
    application
}

group = "ovh.devcraft.kwtransport"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":shared"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
}

application {
    mainClass.set("ChatKt")
    applicationDefaultJvmArgs = listOf("-Djava.library.path=$projectDir/../shared/build/rust-lib/")
}
