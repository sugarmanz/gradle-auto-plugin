rootProject.name = "auto"

// TODO: Maybe make auto JAR solely responsible for wrapping binary
include(
    ":common",
    ":gradle-plugin",
)

// TODO: generate typed version DSL
// Versions.Kotlin
//val kotlinVersion: String get() = settings.extra.properties["versions.kotlin"] as? String ?: ""

pluginManagement {
    val kotlinVersion = settings.extra.properties["versions.kotlin"] as? String ?: ""
    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion

        id("net.researchgate.release") version "2.6.0"
        id("com.jfrog.artifactory") version "4.24.23"
//        id("io.github.gradle-nexus.publish-plugin") version NEXUS_PUBLISH_PLUGIN_VERSION

        id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
//        id("org.jetbrains.kotlinx.binary-compatibility-validator") version VALIDATOR_VERSION
//
//        id("org.jetbrains.dokka") version DOKKA_VERSION
//        id("com.eden.orchidPlugin") version ORCHID_VERSION
    }
}
