FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/app.jar app.jar

# Mount point for nFast libraries
RUN mkdir -p /opt/nfast/toolkits/pkcs11

ENTRYPOINT ["java",
 "-Djava.library.path=/opt/nfast/toolkits/pkcs11",
 "-jar", "app.jar"]