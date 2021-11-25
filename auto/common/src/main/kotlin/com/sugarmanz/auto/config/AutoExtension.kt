package com.sugarmanz.auto.config

import kotlinx.serialization.Serializable

@AutoDslMarker
@Serializable
open class AutoExtension {
    lateinit var repo: String
    lateinit var owner: String

    var githubApi: String? = null
    var githubGraphqlApi: String? = null

    val plugins = Plugins()

    // TODO: Maybe just always translate to string
    lateinit var author: AuthorDeclaration

    // TODO: POC of how to add string setter/getter
    var auth: String get() = when (val author = author) {
        is AuthorDeclaration.FormattedString -> author.value
        is AuthorDeclaration.Explicit -> author.toString()
    }; set(value) {
        author = AuthorDeclaration.FormattedString(value)
    }

    fun author(formatted: AuthorDeclaration.FormattedString) {
        author = formatted
    }

    fun author(formatted: String) = author(AuthorDeclaration.FormattedString(formatted))
}
