package com.sugarmanz.auto.gradle

import com.sugarmanz.auto.gradle.plugins.Plugins
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
import org.gradle.api.Action

@Serializable
open class AutoExtension {
    lateinit var repo: String
    lateinit var owner: String

    var githubApi: String? = null
    var githubGraphqlApi: String? = null

    val plugins = Plugins()

    // TODO: Consider if we need to re-init plugins here
    fun plugins(configure: Action<Plugins>) = configure.execute(plugins)

    // TODO: Maybe just always translate to string
    lateinit var author: AuthorDeclaration

    // TODO: POC of how to add string setter/getter
    var auth: String get() = when (val author = author) {
        is AuthorDeclaration.FormattedString -> author.value
        is AuthorDeclaration.Explicit -> author.toString()
    }; set(value) {
        author = AuthorDeclaration.FormattedString(value)
    }

    fun author(configure: Action<AuthorDeclaration.Explicit>) {
        author = AuthorDeclaration.Explicit().apply(configure::execute)
    }

    fun author(formatted: AuthorDeclaration.FormattedString) {
        author = formatted
    }

    fun author(formatted: String) = author(AuthorDeclaration.FormattedString(formatted))
}

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
//    // TODO: I think it's just name and options: JsonObject
//    open class Configured(val name: String, val options: List<JsonElement>) : PluginDeclaration() {
//
//        constructor(collection: Collection<JsonElement>) : this(collection.first().jsonPrimitive.content, collection.drop(1))
//
//        internal object Serializer : KSerializer<Configured> {
//            private val serializer: KSerializer<JsonArray> = JsonArray.serializer()
//
//            override val descriptor = serializer.descriptor
//
//            override fun serialize(encoder: Encoder, value: Configured) {
//                serializer.serialize(encoder, buildJsonArray {
//                    add(value.name)
//                    value.options.forEach {
//                        add(it)
//                    }
//                })
//            }
//
//            override fun deserialize(decoder: Decoder) = serializer.deserialize(decoder).let(::Configured)
//        }
//
//    }

//    @Serializable(Configured.Serializer::class)
    abstract class Configured<Options>(val name: String, val options: Options) : PluginDeclaration() {

//        constructor(collection: List<JsonElement>) : this(collection.first().jsonPrimitive.content, collection[1].jsonObject)

        companion object {
//            fun <Options> configured(name: String): (Options) -> Configured<Options> = { options ->
//                Configured(name, options)
//            }
        }

        internal open class Serializer<Klass : Configured<Options>, Options>(val optionsSerializer: KSerializer<Options>, val factory: (String, Options) -> Klass) : KSerializer<Klass> {
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

//                serializer.serialize(encoder, if (encodeOptions) buildJsonArray {
//                    add(value.name)
//                    add(options)
//                } else value.name)

//                serializer.serialize(encoder, buildJsonArray {
//                    add(value.name)
//                    add(Json.encodeToJsonElement(optionsSerializer, value.options))
//                })
            }

            override fun deserialize(decoder: Decoder) = serializer.deserialize(decoder).let { (name, options) ->
                name.jsonPrimitive.content to Json.decodeFromJsonElement(optionsSerializer, options)
            }.let { (name, options) -> factory(name, options) }
        }

    }

    @Serializable(Custom.Serializer::class)
    class Custom(name: String, options: JsonObject) : Configured<JsonObject>(name, options) {
        internal object Serializer : Configured.Serializer<Custom, JsonObject>(JsonObject.serializer(), ::Custom)
    }

//    @Serializable(GradlePlugin.Options.serializer()::class)
//    class GradlePlugin(val gradleCommand: String? = null, val gradleOptions: List<String> = emptyList()) : Configured("gradle", buildJsonArray {
//        add(buildJsonObject {
//            put("gradleCommand", gradleCommand)
//            put("gradleOptions", Json.encodeToJsonElement(gradleOptions))
//        })
//    }) {
//
//        @Serializable
//        data class Options(
//            val gradleCommand: String? = null,
//            val gradleOptions: List<String>? = null
//        )
//
//        class Builder {
//            var gradleCommand: String? = null
//            var gradleOptions: List<String> = emptyList()
//
//            internal fun build() = GradlePlugin(gradleCommand, gradleOptions)
//        }
//
//        companion object {
//            fun Plugins.gradle(block: GradlePlugin.Builder.() -> Unit) = Builder().apply(block).build().let(::add)
//        }
//
//        internal object Serializer : KSerializer<GradlePlugin> {
//            private val serializer = Configured.serializer()
//
//            override val descriptor = serializer.descriptor
//
//            override fun deserialize(decoder: Decoder): GradlePlugin {
//                serializer.deserialize(decoder)
//            }
//
//
//            override fun serialize(encoder: Encoder, value: GradlePlugin) {
//                TODO("Not yet implemented")
//            }
//        }
//
//    }

    internal object Serializer : JsonContentPolymorphicSerializer<PluginDeclaration>(PluginDeclaration::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out PluginDeclaration> = when (element) {
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

//abstract class AutoHooks : Hooks() {
////    open val pluginRegistry = syncHook<(name: String) -> >()
//    open val pluginRegistry = syncBailHook<(name: String) -> BailResult<KSerializer<PluginDeclaration>>>()
//}
//
//object Auto {
//    val hooks = AutoHooksImpl()
//}

//@DslMarker
//internal annotation class AutoDslMarker
//
//public inline fun plugins(builderAction: PluginsBuilder.() -> Unit): JsonArray {
//    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
//    val builder = JsonArrayBuilder()
//    builder.builderAction()
//    return builder.build()
//}
//
//class PluginsBuilder()
//
//public inline fun PluginsBuilder.named(builderAction: PluginsBuilder.() -> Unit): JsonArray {
//    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
//    val builder = JsonArrayBuilder()
//    builder.builderAction()
//    return builder.build()
//}
//
///**
// * DSL builder for a [JsonObject]. To create an instance of builder, use [buildJsonObject] build function.
// */
//@AutoDslMarker
//class PluginDeclarationBuilder @PublishedApi internal constructor() {
//
//    private val content: MutableMap<String, JsonElement> = linkedMapOf()
//
//    /**
//     * Add the given JSON [element] to a resulting JSON object using the given [key].
//     *
//     * Returns the previous value associated with [key], or `null` if the key was not present.
//     */
//    public fun put(key: String, element: JsonElement): JsonElement? = content.put(key, element)
//
//    @PublishedApi
//    internal fun build(): JsonObject = JsonObject(content)
//}
