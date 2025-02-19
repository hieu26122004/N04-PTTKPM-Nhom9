FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/AuthService-0.0.1-SNAPSHOT.jar auth.jar


EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/auth.jar"]
