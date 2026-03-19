# API Gateway Dockerfile
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app
# Copy parent pom first
COPY ../pom.xml .
# Copy this service's pom.xml
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM amazoncorretto:17

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENV JAVA_OPTS="-Xms256m -Xmx512m" \
    SPRING_PROFILES_ACTIVE=docker

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]