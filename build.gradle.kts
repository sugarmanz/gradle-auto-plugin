tasks {
    val publish by creating {
        dependsOn(gradle.includedBuild("auto").task(":publish"))
    }
}
