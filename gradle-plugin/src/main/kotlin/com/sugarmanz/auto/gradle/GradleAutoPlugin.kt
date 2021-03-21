package com.sugarmanz.auto.gradle

import net.researchgate.release.ReleaseExtension
import net.researchgate.release.ReleasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.Convention
import kotlin.reflect.typeOf

//import org.gradle.kotlin.dsl.Projec

class GradleAutoPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.extensions.create(
            "auto",
            AutoExtension::class.java
        )

        if (!target.plugins.hasPlugin("net.researchgate.release")) {
            target.plugins.apply("net.researchgate.release")
//            target.plugins.withType(ReleasePlugin::class.java) {
//                it.
//            }
        }

        val process = Runtime.getRuntime().exec("auto")
        if (process.exitValue() != 1) {
            Runtime.getRuntime().exec("cu")
        }
        if (ex)

//        target.configure()<ReleaseExtension>(ReleaseExtension::class.java) {
//            failOnPublishNeeded = false
//        }

//        target.gradle.addListener(
//            object : DependencyResolutionListener {
//                override fun beforeResolve(dependencies: ResolvableDependencies) {
//                    target.addDependency("api", "com.intuit.hooks:hooks:$version")
//                    target.addDependency("compileOnly", "io.arrow-kt:arrow-annotations:$arrowVersion")
//                    target.gradle.removeListener(this)
//                }
//
//                override fun afterResolve(dependencies: ResolvableDependencies) = Unit
//            }
//        )
    }
}

