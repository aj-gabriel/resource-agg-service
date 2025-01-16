# Stage 1: Build the application
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Copy Gradle files and application source code
COPY settings.gradle build.gradle ./
COPY src ./src

# Ensure source code is copied
RUN ls -la /app/src

# Build the application excluding tests
RUN gradle build -x test --no-daemon

# Stage 2: Create the production-ready image with a minimal JRE
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
