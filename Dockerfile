# Use Eclipse Temurin base image for Java 17
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY . /app

RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/quarks-commerce-1.0-SNAPSHOT.jar"] 