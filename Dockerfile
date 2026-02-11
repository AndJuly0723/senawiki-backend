FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /workspace

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew

COPY src src

ARG SKIP_TESTS=true
RUN if [ "$SKIP_TESTS" = "true" ]; then ./gradlew bootJar -x test; else ./gradlew bootJar; fi

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S app && adduser -S app -G app

COPY --from=builder /workspace/build/libs/*.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=prod

USER app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
