# Build stage
FROM maven:3.8.7-openjdk-18-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Install Docker CLI for container orchestration
RUN apt-get update && \
    apt-get install -y docker.io && \
    rm -rf /var/lib/apt/lists/*

# Create directory for temporary code files
RUN mkdir -p /tmp/shodhacode && \
    chmod 777 /tmp/shodhacode

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]