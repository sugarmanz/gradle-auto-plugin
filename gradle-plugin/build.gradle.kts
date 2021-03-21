plugins {
    kotlin("jvm") version "1.4.20"
    kotlin("plugin.serialization") version "1.4.20"
    `java-gradle-plugin`
    `maven-publish`
    id("com.jfrog.artifactory") version "4.13.0"

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
    implementation(kotlin("stdlib"))
    implementation(kotlin("gradle-plugin-api"))
    implementation("net.researchgate", "gradle-release", "2.8.1")
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.0.1")

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

artifactory {
    setContextUrl("https://artifact.intuit.com/artifactory")

    publish(
        delegateClosureOf<org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig> {
            repository(
                delegateClosureOf<groovy.lang.GroovyObject> {
                    setProperty(
                        "repoKey",
                        if (version.toString().contains("SNAPSHOT")) "CG.PD.Intuit-Snapshots" else "CG.PD.Intuit-Releases"
                    )
                    setProperty("username", System.getenv("ARTIFACTORY_USERNAME"))
                    setProperty("password", System.getenv("ARTIFACTORY_PASSWORD"))
                    setProperty("maven", true)
                }
            )

            defaults(
                delegateClosureOf<org.jfrog.gradle.plugin.artifactory.task.ArtifactoryTask> {
                    publications("jar")
                }
            )
        }
    )
}

afterEvaluate {
    configure<PublishingExtension> {
        publications {
            register<MavenPublication>("jar") {
                from(components.getByName("java"))
            }
        }
    }
}
