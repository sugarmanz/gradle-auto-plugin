package com.sugarmanz.npm

import kotlinx.serialization.Serializable

// TODO: Extract into separate package
@Serializable
data class Package(
    val name: Name? = null,
    val version: Semver? = null,
    val description: String? = null,
) {

    init {
    }

}

@Serializable
@JvmInline
value class Name(private val value: String) {

    init {
        // TODO: Not sure these should belong in init or some validate method. My thinking is that someone might potentially read an invalid package.json but still need to operate on it. Maybe validate during serialization?
        require(value.length <= 214) { "The name must be less than or equal to 214 characters. This includes the scope for scoped packages. See https://docs.npmjs.com/cli/v8/configuring-npm/package-json#name for more details." }
        require(scope?.startsWith("@") ?: true) { "Scopes must be preceded by an @ symbol." }
        require(`package`.isNotEmpty()) { "Name must not be empty" }
    }

    private val parts: Pair<String?, String?> get() = (value.split("/") + listOf(null, null))
        .let { (scope, `package`) -> scope to `package` }

    val scope: String? get() = parts.let { (maybeScope, maybePackage) ->
        maybePackage?.let { maybeScope }
    }

//        value.split("/").let {
//        if (it.size == 2) it[0] else null
//    }

    val `package`: String get() = parts.let { (maybeScope, maybePackage) ->
        maybePackage ?: requireNotNull(maybeScope)
    }

}

@Serializable
@JvmInline
value class Semver(private val version: String) {

    init {
        // TODO: Ensure it's parseable by NPM node-semver
    }

    private val parts: Triple<String?, String?, String?> get() = (version.split(".") + listOf(null, null, null))
        .let { (major, minor, patch) -> Triple(major, minor, patch) }

    val major: String? get() = parts.first
    val minor: String? get() = parts.second
    val patch: String? get() = parts.third

}


