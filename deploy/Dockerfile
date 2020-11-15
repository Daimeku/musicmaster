FROM maven:3.6.3-jdk-11-slim as maven

WORKDIR /app/musicmaster

COPY pom.xml .
COPY src src

RUN mvn package -DskipTests

FROM openjdk:16-slim as jdk
COPY --from=maven /app/musicmaster/target/demo-0.0.1-SNAPSHOT.jar musicmaster.jar
ENTRYPOINT ["java", "-jar","musicmaster.jar"]
