import org.gradle.api.Project
import java.io.File

private const val propertiesFileName = "gradle.properties"

/** This is a hack to copy over top-level gradle properties to a composite project included in the top-level project */
fun Project.copyProperties() {
    val topLevelProjectProperties = File(propertiesFileName)
    val includedProjectProperties = File("auto/$propertiesFileName")

    logger.quiet("copying ${topLevelProjectProperties.absolutePath} to ${includedProjectProperties.absolutePath}")

    topLevelProjectProperties.readText().let(includedProjectProperties::writeText)
}
