rootProject.name = "credential-relay-service"

include(":credential-proto")
include(":credential-relay-service")

project(":credential-proto").projectDir = file("credential-proto")
