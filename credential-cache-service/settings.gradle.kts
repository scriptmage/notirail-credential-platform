rootProject.name = "credential-cache-service"

include(":credential-proto")
include(":credential-cache-service")

project(":credential-proto").projectDir = file("credential-proto")
