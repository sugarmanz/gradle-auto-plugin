allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
        maven("https://oss.jfrog.org/artifactory/oss-snapshot-local/")
    }
}

plugins {
    kotlin("jvm") apply false
    `maven-publish`
    id("com.jfrog.artifactory")
}

artifactory {
    setContextUrl("https://artifact.intuit.com/artifactory")

    publish {
        repository {
            setRepoKey(
                if (version.toString().contains("SNAPSHOT")) "CG.PD.Intuit-Snapshots"
                else "CG.PD.Intuit-Releases"
            )
            setUsername(System.getenv("ARTIFACTORY_USERNAME"))
            setPassword(System.getenv("ARTIFACTORY_PASSWORD"))
        }

        defaults {
            publications("jar")
        }
    }
}

tasks {
    named("publish") {
        dependsOn(named("artifactoryPublish"))
    }
}

subprojects {
    apply {
        plugin("kotlin")
        plugin("maven-publish")
    }

    publishing {
        publications {
            register<MavenPublication>("jar") {
                if (project.ext.has("artifactId")) {
                    artifactId = project.ext.get("artifactId") as? String
                }
                from(components["java"])
            }
        }
    }

    dependencies {
        val implementation by configurations
        implementation(kotlin("stdlib"))

        val testImplementation by configurations
        testImplementation(platform("org.junit:junit-bom:5.7.1"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testImplementation("org.junit.jupiter:junit-jupiter-params")
    }

    tasks {
        withType(Test::class.java) {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
            }
        }
    }

}
