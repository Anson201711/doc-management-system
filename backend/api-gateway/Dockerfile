# API Gateway Dockerfile
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy parent pom.xml from backend root
COPY pom.xml .

# Copy this service's pom.xml
COPY pom.xml .

# Create a dummy parent pom for local build (simulates multi-module)
# We'll build without parent resolution
RUN mvn dependency:go-offline -B -N || true

# Copy source and build (skip parent validation)
COPY src ./src
RUN mvn clean package -DskipTests -N || mvn clean package -DskipTests

FROM amazoncorretto:17

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENV JAVA_OPTS="-Xms256m -Xmx512m" \
    SPRING_PROFILES_ACTIVE=docker

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]