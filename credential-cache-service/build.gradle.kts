plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.notirail"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":credential-proto"))

    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("io.grpc:grpc-netty-shaded:1.84.0")
    implementation("io.grpc:grpc-protobuf:1.84.0")
    implementation("io.grpc:grpc-stub:1.84.0")
    implementation("io.grpc:grpc-services:1.84.0")
}
