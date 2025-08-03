FROM maven:3.9.11-eclipse-temurin-21 AS builder

WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw

COPY src ./src
RUN ./mvnw clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
