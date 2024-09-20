FROM openjdk:17-jdk-alpine
ARG JAR_FILE=build/libs/ksis-0.0.1-SNAPSHOT.jar
RUN apk -y update && apk -y upgrade && apk install -y --no-install-recommends ffmpeg && apk clean && rm -rf /var/lib/apt/lists/*
COPY $JAR_FILE app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "./app.jar"]