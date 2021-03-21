repositories {
    jcenter()
    mavenLocal()
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
    maven("https://oss.jfrog.org/artifactory/oss-snapshot-local/")
}

plugins {
    kotlin("jvm") version "1.4.20"
    `java-gradle-plugin`
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
    implementation(kotlin("stdlib"))
    implementation(kotlin("gradle-plugin-api"))
    implementation("net.researchgate", "gradle-release", "2.8.1")

    testImplementation(platform("org.junit:junit-bom:5.7.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

// config JVM target to 1.8 for kotlin compilation tasks
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}
