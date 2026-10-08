# Build stage
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY settings.gradle.kts .
COPY build.gradle.kts .

COPY credential-cache-service/build.gradle.kts \
    credential-cache-service/build.gradle.kts

RUN chmod +x gradlew

RUN ./gradlew :credential-cache-service:dependencies --no-daemon

COPY credential-cache-service/src \
    credential-cache-service/src

RUN ./gradlew :credential-cache-service:bootJar --no-daemon


# Runtime stage
FROM eclipse-temurin:25-jre-noble

WORKDIR /app

RUN groupadd --system app \
    && useradd --system --gid app app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build \
    /app/credential-cache-service/build/libs/*.jar \
    app.jar

RUN chown app:app app.jar

USER app

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
