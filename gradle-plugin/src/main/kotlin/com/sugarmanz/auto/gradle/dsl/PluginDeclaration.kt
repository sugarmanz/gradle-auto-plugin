package com.sugarmanz.auto.gradle.dsl

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable(PluginDeclaration.Serializer::class)
sealed class PluginDeclaration {

    @Serializable(Named.Serializer::class)
    class Named(val name: String) : PluginDeclaration() {

        internal object Serializer : KSerializer<Named> {
            private val serializer: KSerializer<String> =
                String.serializer()

            override val descriptor = PrimitiveSerialDescriptor(
                Named::class.toString(),
                PrimitiveKind.STRING
            )

            override fun serialize(encoder: Encoder, value: Named) =
                serializer.serialize(encoder, value.name)

            override fun deserialize(decoder: Decoder) =
                Named(serializer.deserialize(decoder))
        }
    }

//    @Serializable(Configured.Serializer::class)
    abstract class Configured<Options>(val name: String, val options: Options) : PluginDeclaration() {

        internal open class Serializer<Klass : Configured<Options>, Options>(val optionsSerializer: KSerializer<Options>, val factory: (String, Options) -> Klass) :
            KSerializer<Klass> {
            private val serializer: KSerializer<JsonArray> = JsonArray.serializer()

            override val descriptor = serializer.descriptor

            override fun serialize(encoder: Encoder, value: Klass) {
                val options = Json.encodeToJsonElement(optionsSerializer, value.options)
                val encodeOptions = when (options) {
                    is JsonObject -> options.isNotEmpty()
                    is JsonPrimitive -> true
                    is JsonArray -> options.isNotEmpty()
                    JsonNull -> false
                }

                if (encodeOptions) JsonArray.serializer().serialize(encoder, buildJsonArray {
                    add(value.name)
                    add(options)
                }) else String.serializer().serialize(encoder, value.name)
            }

            override fun deserialize(decoder: Decoder) = serializer.deserialize(decoder).let { (name, options) ->
                name.jsonPrimitive.content to Json.decodeFromJsonElement(optionsSerializer, options)
            }.let { (name, options) -> factory(name, options) }
        }

    }

    @Serializable(Custom.Serializer::class)
    class Custom(name: String, options: JsonObject) : Configured<JsonObject>(name, options) {
        internal object Serializer : Configured.Serializer<Custom, JsonObject>(JsonObject.serializer(),
            PluginDeclaration::Custom
        )
    }

    internal object Serializer : JsonContentPolymorphicSerializer<PluginDeclaration>(PluginDeclaration::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out PluginDeclaration> = when (element) {
            // TODO: Potential hooks integration for serializer registration of known plugin configuration types
//            is JsonArray -> Auto.hooks.pluginRegistry.call(element.first().jsonPrimitive.content).let {
//                it ?: Custom.Serializer
////                Configured.serializer(it ?: JsonObject.serializer())
//            }
            is JsonArray -> Custom.Serializer
            is JsonPrimitive -> Named.serializer()
            else -> throw SerializationException("element of type ${element::class.simpleName} not supported")
        }
    }

}