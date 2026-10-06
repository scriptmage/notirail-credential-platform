plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.google.protobuf") version "0.10.0"
}

group = "com.notirail"
version = "0.0.1-SNAPSHOT"

dependencies {
    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // gRPC
    implementation("io.grpc:grpc-netty-shaded:1.84.0")
    implementation("io.grpc:grpc-protobuf:1.84.0")
    implementation("io.grpc:grpc-stub:1.84.0")

    // Protobuf
    implementation("com.google.protobuf:protobuf-java:4.30.2")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.30.2"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.84.0"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc") {
                    option("OmitReflectionServiceDescriptor")
                }
            }
        }
    }
}
