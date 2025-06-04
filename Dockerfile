FROM maven:3.9.5-eclipse-temurin-17 AS builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk

RUN apt-get update && \
    apt-get install -y docker.io && \
    apt-get clean \

WORKDIR /app
COPY --from=builder /app/target/osint-recon.jar osint-recon.jar

ENTRYPOINT ["java", "-jar", "osint-recon.jar"]