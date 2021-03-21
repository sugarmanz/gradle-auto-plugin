allprojects {
    repositories {
        jcenter()
        mavenLocal()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
        maven("https://oss.jfrog.org/artifactory/oss-snapshot-local/")
    }
}