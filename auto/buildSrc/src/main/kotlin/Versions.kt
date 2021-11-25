import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.support.delegates.SettingsDelegate
import kotlin.reflect.KProperty


typealias Properties = Map<String, *>

fun Properties.version(name: String) = get("versions.$name") as String

class PropertiesDelegate(val properties: Map<String, *>, val name: String? = null) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = properties.version(name ?: property.name)
}

fun Project.versions(name: String? = null) = PropertiesDelegate(properties, name)

val Project.versions get() = versions()

fun Project.version(name: String) = properties.version(name)

fun Settings.versions(name: String? = null) = PropertiesDelegate(extra.properties, name)

val Settings.versions get() = versions()

fun Settings.version(name: String) = extra.properties.version(name)

fun SettingsDelegate.versions(name: String? = null) = settings.versions(name)

val SettingsDelegate.versions get() = versions()

fun SettingsDelegate.version(name: String) = settings.version(name)

