# ----- Stage 1: Build ứng dụng với Maven -----
FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /app

# Sao chép toàn bộ mã nguồn vào image
COPY . .

# Biên dịch và đóng gói ứng dụng, bỏ qua test để tăng tốc
RUN mvn clean package -DskipTests

# ----- Stage 2: Runtime với JDK nhẹ hơn -----
FROM openjdk:21-jdk-slim
WORKDIR /app

# Sao chép file .war đã được tạo ở stage build
COPY --from=build /app/target/*.war app.war

# Mở cổng 8080
EXPOSE 8080

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.war"]
