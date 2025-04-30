# ----- Stage 1: Build ứng dụng với Maven -----
FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .

# Đóng gói và in log rõ ràng
RUN mvn clean package -DskipTests

# ----- Stage 2: Runtime -----
FROM openjdk:21-jdk-slim
WORKDIR /app

# Sao chép file WAR (cập nhật tên chính xác)
COPY --from=build /app/target/*.war /app/app.war

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.war"]
