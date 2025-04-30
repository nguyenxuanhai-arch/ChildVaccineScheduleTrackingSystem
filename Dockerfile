# ----- Stage 1: Build ứng dụng với Maven -----
FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

# ----- Stage 2: Runtime -----
FROM openjdk:21-jdk-slim
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
