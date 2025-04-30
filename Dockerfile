# Sử dụng hình ảnh chính thức của OpenJDK 21
FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Chuyển sang hình ảnh OpenJDK 21 để chạy ứng dụng
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/Demo-0.0.1-SNAPSHOT.war app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war"]
