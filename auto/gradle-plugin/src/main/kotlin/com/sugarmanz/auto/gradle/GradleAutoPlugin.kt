package com.sugarmanz.auto.gradle

import com.sugarmanz.auto.config.AutoExtension
import com.sugarmanz.auto.gradle.extensions.exec
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.researchgate.release.ReleaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.*
import java.io.IOException

class GradleAutoPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.extensions.create(
            "auto",
            AutoExtension::class.java
        )

        if (!target.tasks.any { it.name == "build" }) {
            val build by target.tasks.registering {
                group = "build"
            }
        }

        if (!target.plugins.hasPlugin("net.researchgate.release")) {
            target.plugins.apply("net.researchgate.release")

            target.configure<ReleaseExtension> {
                failOnPublishNeeded = false
            }
        }

        val auto = target.getAuto().apply {
            exec()
        }

        target.tasks {

            // TODO: Create task types
            val autorc by registering {
                group = "auto"
                doLast {
                    val extension = target.extensions.findByType(AutoExtension::class.java)
                        ?: AutoExtension()

                    target.projectDir.resolve(".autorc").writeText(Json {
                        prettyPrint = true
                    }.encodeToString(extension))
                }
            }

            val shipit by registering {
                group = "auto"
                dependsOn(autorc)
                doLast {
                    require(listOfNotNull(
                        auto,
                        "shipit",
                        if (target.properties["auto.dryRun"] == "true") "--dry-run" else null,
                        *((target.properties["auto.freeArgs"] as? String)?.split(",".toRegex())?.toTypedArray()
                            ?: emptyArray())
                    ).exec { println(it) } == 0) { "shipit did not succeed" }
                }
            }

            val createReleaseTag: Task by getting {
                enabled = false
            }

            val preTagCommit: Task by getting {
                enabled = false
            }

            val commitNewVersion: Task by getting {
                enabled = false
            }
        }
    }

    /** Extension to determine what auto to use */
    // TODO: Incorporate versioning
    private fun Project.getAuto(): String {
        // is auto on path
        try {
            "auto".exec()
            return "auto"
        } catch (exception: IOException) {}

        // is auto in build
        try {
            "${buildDir}/auto".exec()
        } catch (exception: IOException) {
            // okay, let's get it
            "mkdir $buildDir".exec()
            // TODO: need to configure multiplatform support
            "curl -kL -o ${buildDir.resolve("auto.gz")} https://github.com/intuit/auto/releases/download/v10.16.8/auto-macos.gz".exec()
            "gunzip ${buildDir.resolve("auto.gz")}".exec()
            "chmod a+x ${buildDir.resolve("auto")}".exec()
        }

        return "${buildDir}/auto"
    }
}
