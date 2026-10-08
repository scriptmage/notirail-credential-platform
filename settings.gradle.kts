rootProject.name = "credential-platform"

if (file("credential-cache-service").isDirectory) {
    include("credential-cache-service")
}

if (file("credential-relay-service").isDirectory) {
    include("credential-relay-service")
}

if (file("credential-service").isDirectory) {
    include("credential-service")
}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("io.freefair.lombok") version "8.10"
    }
}
