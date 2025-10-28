FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
RUN apk add --no-cache wget
RUN wget https://github.com/jwilder/dockerize/releases/download/v0.6.1/dockerize-alpine-linux-amd64-v0.6.1.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-alpine-linux-amd64-v0.6.1.tar.gz \
    && rm dockerize-alpine-linux-amd64-v0.6.1.tar.gz
COPY --from=build /app/target/auction-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["dockerize", "-wait", "tcp://sqlserver:1433", "-timeout", "60s", "java", "-jar", "app.jar"]
