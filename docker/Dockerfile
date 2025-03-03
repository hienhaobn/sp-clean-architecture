ARG HOST_UID=1000
ARG HOST_GID=1000

# Build stage
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src src/

RUN apt-get update && \
    apt-get install -y maven && \
    ./mvnw clean package -DskipTests

# Development image
FROM eclipse-temurin:21-jdk-jammy AS dev

# Tạo nhóm và người dùng
RUN groupadd -g 1000 appgroup && \
    useradd -u 1000 -g appgroup -m -d /home/appuser appuser

# Tạo thư mục làm việc
WORKDIR /app

# 1. Copy Maven Wrapper trước để tận dụng cache
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# 2. Tạo thư mục .m2 trong thư mục home và thiết lập quyền
RUN mkdir -p /home/appuser/.m2 && \
    chown -R appuser:appgroup /home/appuser/.m2 && \
    chmod +x mvnw && \
    ./mvnw dependency:go-offline -B

# 3. Copy source code
COPY src src/

# 4. Chạy ứng dụng bằng Maven Wrapper
CMD ["./mvnw", "spring-boot:run"]

# Security metadata
LABEL org.opencontainers.image.authors="Hien Hao"
LABEL org.opencontainers.image.source="https://github.com/hienhaobn"
