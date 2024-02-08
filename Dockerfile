FROM openjdk:17-jdk-slim
WORKDIR /app
COPY "/target/obd-0.0.1-SNAPSHOT.jar" "/app/obd-0.0.1-SNAPSHOT.jar"
CMD ["java", "-jar", "obd-0.0.1-SNAPSHOT.jar"]

