FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/hsm-demo.jar app.jar

ENTRYPOINT ["java",
 "-Djava.library.path=/opt/nfast/toolkits/pkcs11",
 "-jar",
 "app.jar"]