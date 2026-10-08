plugins {
	java
	id("org.springframework.boot") version "4.1.1" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
}

subprojects {
	plugins.apply("java")
	plugins.apply("org.springframework.boot")
	plugins.apply("io.spring.dependency-management")

	java {
		sourceCompatibility = JavaVersion.VERSION_25
		targetCompatibility = JavaVersion.VERSION_25
	}

	repositories {
		mavenCentral()
	}

	dependencies {
		// Spring Boot 4
        implementation("org.springframework.boot:spring-boot-starter-web")
		implementation("org.springframework.boot:spring-boot-starter-actuator")
        implementation("org.springframework.cloud:spring-cloud-config-client:5.0.5")
        implementation("org.springframework.boot:spring-boot-starter-validation")
		testImplementation("org.springframework.boot:spring-boot-starter-test")

        // Micrometer
        implementation("io.micrometer:micrometer-core")
        implementation("io.micrometer:micrometer-registry-prometheus")

		// Lombok
		compileOnly("org.projectlombok:lombok")

		annotationProcessor("org.projectlombok:lombok")

		testCompileOnly("org.projectlombok:lombok")
		testAnnotationProcessor("org.projectlombok:lombok")
	}
}
