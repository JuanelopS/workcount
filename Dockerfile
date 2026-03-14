#build
FROM maven:3.9.5-eclipse-temurin-17-alpine AS build

WORKDIR /app

COPY pom.xml .
RUN mvc dependency:go-offline

COPY src ./src
RUN maven clean package -DskipTests

#run
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]