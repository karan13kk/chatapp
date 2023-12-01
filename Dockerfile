# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17-slim AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -e clean install -DskipTests

# Stage 2: Copy jar file
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8000
ENTRYPOINT ["java", "-Dspring.config.location=classpath:/application.properties", "-jar", "app.jar"]