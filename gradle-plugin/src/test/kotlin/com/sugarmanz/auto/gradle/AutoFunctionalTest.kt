package com.sugarmanz.auto.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class BuildLogicFunctionalTest {

    @TempDir
    lateinit var testProjectDir: File
    private lateinit var settingsFile: File
    private lateinit var buildFile: File
    private lateinit var propertiesFile: File
    private val autorcFile: File get() = testProjectDir.resolve(".autorc")

    @BeforeEach
    fun setup() {
        settingsFile = testProjectDir.resolve("settings.gradle.kts").apply {
            createNewFile()
        }
        buildFile = testProjectDir.resolve("build.gradle.kts").apply {
            createNewFile()
        }
        propertiesFile = testProjectDir.resolve("gradle.properties").apply {
            createNewFile()
        }
        testProjectDir.resolve("output.txt").apply {
            createNewFile()
        }
        testProjectDir.resolve(".env").apply {
            createNewFile()
            writeText("GH_TOKEN=abc")
        }
    }

    @Test
    fun `test updateVersion task`() {
        settingsFile.writeText("""
            rootProject.name = "hello-world"
        """.trimIndent())
        buildFile.writeText("""
            plugins {
                id("com.sugarmanz.auto")
            }
            
            auto {
                owner = "some-org"
                repo = "some-repo"
                author {
                    name = "JZ"
                    email = "j@z.com"
                }
            }
            
            tasks.register("build") {}
        """.trimIndent())
        propertiesFile.writeText("""
            version=0.0.0-SNAPSHOT
        """.trimIndent())

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("updateVersion", "-Prelease.useAutomaticVersion=true", "-Prelease.newVersion=0.0.1")
            .withPluginClasspath()
            .withDebug(true)
            .forwardOutput()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":updateVersion")?.outcome)
        assertEquals("version=0.0.1", propertiesFile.readText())
    }

    @Test
    fun `test shipit task`() {
        settingsFile.writeText("""
            rootProject.name = "hello-world"
        """.trimIndent())
        buildFile.writeText("""
            import com.sugarmanz.auto.gradle.plugins.gradle
            
            plugins {
                id("com.sugarmanz.auto")
            }
            
            auto {
                owner = "some-org"
                repo = "some-repo"
                author {
                    name = "JZ"
                    email = "j@z.com"
                }
                
                plugins {
                    gradle()
                }
            }
            
            tasks.register("build") {}
        """.trimIndent())
        propertiesFile.writeText("""
            version=0.0.0-SNAPSHOT
        """.trimIndent())

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("shipit", "-Pauto.freeArgs=-vvv", "-Pauto.dryRun=true", "--stacktrace")
            .withPluginClasspath()
            .forwardOutput()
            .withDebug(true)
            .buildAndFail()

        assertEquals(TaskOutcome.SUCCESS, result.task(":autorc")?.outcome)
        assertEquals(TaskOutcome.FAILED, result.task(":shipit")?.outcome)
        // TODO: Can't test actual auto until i figure out how to mock github, maybe local git repo?
        assertEquals("version=0.0.0-SNAPSHOT", propertiesFile.readText())
    }

    @Test
    fun `test autorc task`() {
        settingsFile.writeText("""
            rootProject.name = "hello-world"
        """.trimIndent())
        buildFile.writeText("""
            plugins {
                id("com.sugarmanz.auto")
            }
            
            auto {
                owner = "some-org"
                repo = "some-repo"
                author {
                    name = "JZ"
                    email = "j@z.com"
                }
            }
            
            tasks.register("build") {}
        """.trimIndent())

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("autorc", "--stacktrace")
            .withPluginClasspath()
            .forwardOutput()
            .withDebug(true)
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":autorc")?.outcome)
        assertEquals("""
{
    "repo": "some-repo",
    "owner": "some-org",
    "plugins": [
    ],
    "author": {
        "name": "JZ",
        "email": "j@z.com"
    }
}
        """.trimIndent(), autorcFile.readText())
    }
}