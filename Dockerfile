FROM openjdk:22-jdk
COPY target/library_app-0.0.1-SNAPSHOT.jar application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]