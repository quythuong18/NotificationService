FROM amazoncorretto:17-alpine-jdk
LABEL maintainer="quythuong"
COPY target/NotificationService-0.0.1-SNAPSHOT.jar NotificationService.jar
ENTRYPOINT ["java", "-jar", "/NotificationService.jar"]
