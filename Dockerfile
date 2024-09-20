FROM openjdk:17-jdk-alpine
ARG JAR_FILE=build/libs/ksis-0.0.1-SNAPSHOT.jar
RUN apt-get -y update && apt-get -y upgrade && apt-get install -y --no-install-recommends ffmpeg && apt-get clean && rm -rf /var/lib/apt/lists/*
COPY $JAR_FILE app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "./app.jar"]