package com.sugarmanz.auto.gradle.extensions

import java.io.File
import java.util.concurrent.TimeUnit

fun List<String>.exec(workingDir: File = File("./"), outputConsumer: (String) -> Unit = {}): Int = this
    .also { println("Exec: ${joinToString(" ")}")}
    .let(::ProcessBuilder)
    .directory(workingDir)
    .redirectOutput(ProcessBuilder.Redirect.PIPE)
    .redirectError(ProcessBuilder.Redirect.PIPE)
    .start().apply {
        inputStream.bufferedReader().readText().also(outputConsumer)
        errorStream.bufferedReader().readText().also(outputConsumer)
        waitFor(60, TimeUnit.MINUTES)
    }.exitValue()

fun String.exec(workingDir: File = File("./"), outputConsumer: (String) -> Unit = {}): Int = split("\\s".toRegex())
    .exec(workingDir, outputConsumer)
