package com.sugarmanz.auto.gradle.extensions

import com.sugarmanz.auto.config.AuthorDeclaration
import com.sugarmanz.auto.config.AutoExtension
import com.sugarmanz.auto.config.Plugins
import org.gradle.api.Action

// TODO: Consider if we need to re-init plugins here
fun AutoExtension.plugins(configure: Action<Plugins>) = configure.execute(plugins)

fun AutoExtension.author(configure: Action<AuthorDeclaration.Explicit>) {
    author = AuthorDeclaration.Explicit().apply(configure::execute)
}
