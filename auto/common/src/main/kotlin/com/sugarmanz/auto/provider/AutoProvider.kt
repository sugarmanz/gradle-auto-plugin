package com.sugarmanz.auto.provider

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

sealed class AutoProvider {
    abstract val version: String
}

/// node based
/// yarn based
//class YarnInstaller(val packageJson: JsonObject) : AutoProvider() {
//
//    constructor(version: String) : this()
//
//    override val version by lazy {
//        packageJson.getOrElse("dependencies")
//    }
//
//    companion object {
//
//        fun
//
//    }
//
//}
//
//
//
///// binary based -- needs platform
//class BinaryInstaller : AutoProvider