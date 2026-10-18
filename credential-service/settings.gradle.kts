rootProject.name = "credential-service"

include(":credential-api")
include(":credential-proto")
include(":credential-service")

project(":credential-api").projectDir = file("credential-api")
project(":credential-proto").projectDir = file("credential-proto")
