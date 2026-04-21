# ---------- BUILD STAGE ----------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ---------- RUNTIME STAGE ----------
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java","-Djava.library.path=/opt/nfast/toolkits/pkcs11","-jar","app.jar"]