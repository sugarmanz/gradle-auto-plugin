rootProject.name = "gradle-auto-plugin"

include(
    ":examples:simple-gradle-example",
    ":examples:simple-gradle-kts-example",
)

pluginManagement {
    includeBuild("auto")
}
