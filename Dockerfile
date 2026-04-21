# ---------- BUILD STAGE ----------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests


# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:17-jdk

WORKDIR /app

# ✅ Copy application
COPY --from=build /app/target/*.jar app.jar

# ==============================
# 🔥 IMPORTANT: nCipher HSM support
# ==============================

# OPTION 1 (RECOMMENDED): mount at runtime
# /opt/nfast should be mounted from host

# ENTRYPOINT
ENTRYPOINT ["java",
 "-Djava.library.path=/opt/nfast/toolkits/pkcs11",
 "-jar",
 "app.jar"]