# Stage 1: Build bằng Maven (Dùng bản mới eclipse-temurin cho ổn định)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Chạy bằng Eclipse Temurin (Thay thế cho openjdk:17-jdk-slim đã lỗi)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]