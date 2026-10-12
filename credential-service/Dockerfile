# Build stage
# Build stage
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY settings.gradle.kts .
COPY build.gradle.kts .

COPY credential-api/build.gradle.kts ./credential-api/build.gradle.kts
COPY credential-api/src ./credential-api/src

COPY credential-service/build.gradle.kts ./credential-service/build.gradle.kts

RUN chmod +x gradlew

RUN ./gradlew :credential-api:publishToMavenLocal -PapiVersion=0.0.1 --no-daemon

COPY credential-service/src ./credential-service/src

RUN ./gradlew :credential-service:bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:25-jre-noble

WORKDIR /app

RUN groupadd --system app \
    && useradd --system --gid app app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build \
    /app/credential-service/build/libs/*.jar \
    app.jar

RUN chown app:app app.jar

USER app

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
