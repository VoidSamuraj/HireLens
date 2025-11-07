# Stage 1: builder - Java + NodeJS do budowy aplikacji Java + React
FROM gradle:jdk21 AS builder

ARG REACT_APP_API_URL
ENV REACT_APP_API_URL=$REACT_APP_API_URL

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Dajemy wykonanie wrapperowi gradle
RUN chmod +x ./gradlew

# Budujemy aplikację (w tym frontend jeśli jest uruchamiany w gradle)
RUN ./gradlew clean build -x test

# Stage 2: runtime - lżejszy obraz do uruchomienia jar
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
