package com.sugarmanz.auto.gradle

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@Serializable(AuthorDeclaration.Serializer::class)
sealed class AuthorDeclaration {

    @Serializable(FormattedString.Serializer::class)
    class FormattedString(val value: String) : AuthorDeclaration() {

        init {
            // TODO: Validate value
        }

        internal object Serializer : KSerializer<FormattedString> {
            private val serializer: KSerializer<String> =
                String.serializer()

            override val descriptor = PrimitiveSerialDescriptor(
                PluginDeclaration.Named::class.toString(),
                PrimitiveKind.STRING
            )

            override fun serialize(encoder: Encoder, value: FormattedString) =
                serializer.serialize(encoder, value.value)

            override fun deserialize(decoder: Decoder) =
                FormattedString(serializer.deserialize(decoder))
        }

    }

    @Serializable
    class Explicit : AuthorDeclaration() {

        lateinit var name: String
        lateinit var email: String

        fun toFormatted() = FormattedString("$name <$email>")

        override fun toString() = toFormatted().value

    }

    internal object Serializer : JsonContentPolymorphicSerializer<AuthorDeclaration>(AuthorDeclaration::class) {
        override fun selectDeserializer(element: JsonElement) = when (element) {
            is JsonObject -> Explicit.serializer()
            is JsonPrimitive -> FormattedString.serializer()
            else -> throw SerializationException("element of type ${element::class.simpleName} not supported")
        }
    }

}