# User Service Dockerfile
# 多阶段构建优化镜像大小

# 构建阶段
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# 复制依赖文件
COPY pom.xml .
COPY src ./src

# 下载依赖并构建
RUN mvn clean package -DskipTests

# 生产镜像
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 复制构建产物
COPY --from=builder /app/target/*.jar app.jar

# 创建必要目录
RUN mkdir -p /app/storage /app/logs

# 环境变量
ENV JAVA_OPTS="-Xms256m -Xmx512m" \
    SPRING_PROFILES_ACTIVE=docker

EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]