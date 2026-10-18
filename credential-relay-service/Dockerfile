FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

COPY --from=gradle-root gradlew .
COPY --from=gradle-root gradle ./gradle

COPY settings.gradle.kts ./settings.gradle.kts
COPY --from=gradle-root build.gradle.kts ./build.gradle.kts

COPY build.gradle.kts ./credential-relay-service/build.gradle.kts
COPY src ./credential-relay-service/src

COPY --from=proto . ./credential-proto

RUN chmod +x gradlew

RUN ./gradlew :credential-relay-service:bootJar --no-daemon

FROM eclipse-temurin:25-jre-noble

WORKDIR /app

RUN groupadd --system app \
    && useradd --system --gid app app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/credential-relay-service/build/libs/*.jar app.jar

RUN chown app:app app.jar

USER app

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
