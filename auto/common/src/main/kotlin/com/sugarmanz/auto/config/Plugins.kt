package com.sugarmanz.auto.config

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject

@AutoDslMarker
@Serializable(Plugins.Serializer::class)
// TODO: Configure gradle plugin by default
class Plugins(private val plugins: MutableList<PluginDeclaration> = mutableListOf()) : MutableList<PluginDeclaration> by plugins {

    // TODO: Maybe override add to ensure that duplicates don't exist
    fun add(name: String, options: JsonObject) = add(if (options.isEmpty())
        PluginDeclaration.Named(name) else PluginDeclaration.Custom(name, options)
    )
    fun add(name: String, configure: JsonObjectBuilder.() -> Unit = {}) = add(name, buildJsonObject(configure))

    // TODO: Potentially replicate plugins block DSL id("some-id") {} version <- Might support yarn dependencies
    // fun id(name: String)

    // TODO: Write a delegated serializer
    class Serializer : KSerializer<Plugins> {
        private val serializer = ListSerializer(PluginDeclaration.serializer())

        override val descriptor = serializer.descriptor

        override fun deserialize(decoder: Decoder) = serializer.deserialize(decoder).toMutableList().let(::Plugins)

        override fun serialize(encoder: Encoder, value: Plugins) {
            serializer.serialize(encoder, value.plugins)
        }
    }
}