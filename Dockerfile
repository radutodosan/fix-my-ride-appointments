# use existing image as base
FROM openjdk:21-jdk

VOLUME /tmp

# retrieve needed files and dependencies
COPY ./target/appointments-0.0.1-SNAPSHOT.jar appointments.jar

# specify a start-up command
ENTRYPOINT ["java", "-jar", "/appointments.jar"]
