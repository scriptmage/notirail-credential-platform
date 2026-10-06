rootProject.name = "credential-platform"

include("credential-service")
include("credential-relay-service")
include("credential-cache-service")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("io.freefair.lombok") version "8.10"
    }
}
