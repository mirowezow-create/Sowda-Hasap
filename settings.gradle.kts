pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "SowdaHasap"
include(":android", ":desktop", ":keygen-android", ":keygen-desktop")
project(":keygen-android").projectDir = file("keygen/android")
project(":keygen-desktop").projectDir = file("keygen/desktop")
