# Vanniktech Maven Publish Plugin

The `com.vanniktech.maven.publish` plugin is the community standard for publishing Kotlin Multiplatform (KMP) libraries. it simplifies the complex task of managing multiple artifacts (JVM, Android, WASM, JS) and ensures compliance with Maven Central requirements.

## 1. Key Benefits for KMP
- **Automatic Target Detection:** Detects `org.jetbrains.kotlin.multiplatform` and creates publications for all configured targets.
- **Unified Configuration:** Provides a single API to configure POM, SCM, and License info across all project types.
- **In-Memory GPG Signing:** Easily sign artifacts on CI (GitHub Actions) without storing physical key files on the runner.
- **Maven Central Ready:** Built-in support for Sonatype Central Portal, including automatic release and staging management.

## 2. Basic Setup

### A. Version Catalog (`gradle/libs.versions.toml`)
```toml
[plugins]
vanniktech-publish = { id = "com.vanniktech.maven.publish", version = "0.30.0" }
```

### B. Build Configuration (`kwtransport/build.gradle.kts`)
```kotlin
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.vanniktech.publish)
}

mavenPublishing {
    // coordinates(group, artifactId, version)
    coordinates("io.github.arosov", "kwtransport", "0.1.0-SNAPSHOT")

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

    // Recommended for KMP: Use Dokka for Javadoc
    configure(
        com.vanniktech.maven.publish.JavadocJar.Dokka("dokkaHtml"),
        com.vanniktech.maven.publish.SourcesJar.Sources()
    )
}
```

## 3. Publishing to Maven Central (Sonatype)

### A. Local Credentials (`~/.gradle/gradle.properties`)
*NEVER commit these.*
```properties
mavenCentralUsername=myUser
mavenCentralPassword=myPassword
# For signing
signing.keyId=12345678
signing.password=keyPassword
signing.secretKeyRingFile=/home/user/.gnupg/secring.gpg
```

### B. CI Credentials (GitHub Secrets)
For GitHub Actions, the plugin supports in-memory keys:
- `ORG_GRADLE_PROJECT_mavenCentralUsername`
- `ORG_GRADLE_PROJECT_mavenCentralPassword`
- `ORG_GRADLE_PROJECT_signingInMemoryKey` (The exported GPG private key)
- `ORG_GRADLE_PROJECT_signingInMemoryKeyPassword`

## 4. Useful Tasks
- `./gradlew publishToMavenLocal`: Test the publication locally.
- `./gradlew publishAllPublicationsToMavenCentralRepository`: Upload everything to Sonatype.
- `./gradlew publishToMavenCentral --no-configuration-cache`: Publish and automatically close/release the staging repository.

## 5. Official Resources
- [Plugin Documentation](https://vanniktech.github.io/gradle-maven-publish-plugin/)
- [GitHub Repository](https://github.com/vanniktech/gradle-maven-publish-plugin)
