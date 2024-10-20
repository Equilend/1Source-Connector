FROM maven:3.9.5-amazoncorretto-17 AS build

COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests
FROM amazoncorretto:17-al2-jdk
ARG JAR_FILE=target/*.jar
COPY --from=build /home/app/target/*.jar /usr/local/lib/app.jar
ENTRYPOINT ["java", "-jar", "/usr/local/lib/app.jar"]
