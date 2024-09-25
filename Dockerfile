FROM openjdk:17-jdk-alpine
ARG JAR_FILE=build/libs/ksis-0.0.1-SNAPSHOT.jar
RUN apk add --no-cache ffmpeg
COPY $JAR_FILE app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "./app.jar"]