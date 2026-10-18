rootProject.name = "credential-platform"

include("credential-api")
include("credential-proto")
include("credential-service")
include("credential-cache-service")
include("credential-relay-service")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("io.freefair.lombok") version "8.10"
    }
}
