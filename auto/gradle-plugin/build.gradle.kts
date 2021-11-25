plugins {
    kotlin("plugin.serialization")
    `java-gradle-plugin`

    // TODO: Maybe just configure the dependency
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        create("GradleAutoPlugin") {
            id = "com.sugarmanz.auto"
            implementationClass = "com.sugarmanz.auto.gradle.GradleAutoPlugin"
        }
    }
}

dependencies {
    implementation(project(":common"))

    implementation(kotlin("gradle-plugin-api"))
    implementation("net.researchgate", "gradle-release", "2.8.1")
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", version("kotlinx.serialization"))
}
