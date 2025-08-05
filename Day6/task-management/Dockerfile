# -------- STAGE 1: Build the application --------
FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

# -------- STAGE 2: Run the application --------
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY --from=builder /app/target/taskmanagement-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
