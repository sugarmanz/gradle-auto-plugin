package com.sugarmanz.auto.gradle.plugins

import com.sugarmanz.auto.gradle.dsl.PluginDeclaration
import com.sugarmanz.auto.gradle.dsl.Plugins
import kotlinx.serialization.Serializable

// TODO: Can we generate these from TS types?
@Serializable
data class Options(
    var gradleCommand: String? = null,
    var gradleOptions: List<String>? = null
)

@Serializable(GradlePlugin.Serializer::class)
class GradlePlugin(options: Options) : PluginDeclaration.Configured<Options>("gradle", options) {

    private constructor(name: String, options: Options) : this(options)

    internal object Serializer : Configured.Serializer<GradlePlugin, Options>(Options.serializer(), ::GradlePlugin)

}

fun Plugins.gradle(configure: Options.() -> Unit = {}) = Options().apply(configure).let(::GradlePlugin).let(::add)

// TODO: Evaluate if this is a good API
val Plugins.gradle get() = Options().let(::GradlePlugin).let(::add)
