FROM openjdk:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=recruitment-task-ultimate-systems-0.0.1-SNAPSHOT.jar
COPY ./target/${JAR_FILE} app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]