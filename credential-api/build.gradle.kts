plugins {
    java
    `java-library`
    `maven-publish`
    id("org.openapi.generator") version "7.15.0"
}

val apiVersion: String = project.findProperty("apiVersion")
    ?.toString()
    ?: System.getenv("API_VERSION")
    ?: "0.0.0-SNAPSHOT"

group = "com.notirail"
version = apiVersion

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.1.1"))

    implementation("jakarta.servlet:jakarta.servlet-api")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("jakarta.annotation:jakarta.annotation-api")

    implementation("org.springframework:spring-web")
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-core")

    implementation("io.swagger.core.v3:swagger-annotations-jakarta:2.2.39")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.3")
}

openApiGenerate {
    generatorName.set("spring")
    inputSpec.set(
        layout.buildDirectory
            .file("resources/main/openapi/credential-service.yml")
            .get()
            .asFile
            .absolutePath
    )
    outputDir.set(layout.buildDirectory.dir("generated/openapi").get().asFile.absolutePath)

    apiPackage.set("com.notirail.credential.api")
    modelPackage.set("com.notirail.credential.api.model")
    invokerPackage.set("com.notirail.credential.api.invoker")

    configOptions.set(
        mapOf(
            "useSpringBoot3" to "true",
            "interfaceOnly" to "true",
            "useTags" to "true",
            "useBeanValidation" to "true",
            "openApiNullable" to "false",
            "skipDefaultInterface" to "true",
            "library" to "spring-boot"
        )
    )
}

tasks.processResources {
    filesMatching("**/openapi*.yml") {
        expand("API_VERSION" to apiVersion)
    }
}

tasks.named("openApiGenerate") {
    dependsOn("processResources")
}

sourceSets {
    main {
        java {
            srcDir(layout.buildDirectory.dir("generated/openapi/src/main/java"))
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

tasks.named("compileJava") {
    dependsOn("openApiGenerate")
}

tasks.named("sourcesJar") {
    dependsOn("openApiGenerate")
}
