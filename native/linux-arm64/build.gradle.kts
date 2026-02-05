import java.util.Properties

plugins {
    `java-library`
    alias(libs.plugins.vanniktech.publish)
}

// Load local.properties into project properties
if (file("../../local.properties").exists()) {
    val localProperties = Properties()
    file("../../local.properties").inputStream().use { localProperties.load(it) }
    localProperties.forEach { key, value ->
        project.extensions.extraProperties.set(key.toString(), value)
    }
}

// Version management
val envVersion = System.getenv("VERSION")
val finalVersion = if (!envVersion.isNullOrBlank()) {
    envVersion
} else {
    project.findProperty("VERSION_NAME")?.toString() ?: "0.0.0"
}

project.version = finalVersion
project.group = project.findProperty("GROUP")?.toString() ?: "ovh.devcraft"

mavenPublishing {
    coordinates(project.group.toString(), "kwtransport-jvm-linux-arm64", project.version.toString())

    pom {
        name.set("kwtransport-jvm-linux-arm64")
        description.set("Linux arm64 native binary for kwtransport")
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

    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}
