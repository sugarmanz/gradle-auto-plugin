rootProject.name = "auto"

// TODO: Maybe make auto JAR solely responsible for wrapping binary
include(
    ":common",
    ":gradle-plugin",
)

pluginManagement {
    fun Map<String, *>.version(name: String) = get("versions.$name") as? String ?: name
    fun Settings.version(name: String) = extra.properties.version(name)
    fun version(name: String) = settings.version(name)
    infix fun PluginDependencySpec.version(name: String) = this@version.version(version(name))

    plugins {
        kotlin("jvm") version "kotlin"
        kotlin("plugin.serialization") version "kotlin"

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
