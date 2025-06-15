FROM openjdk:17
WORKDIR /app

ARG ACTIVE_PROFILE=prod
ARG JAR_FILE=build/libs/*.jar

ENV SPRING_PROFILES_ACTIVE=$ACTIVE_PROFILE

COPY ${JAR_FILE} app.jar

LABEL authors="kyunghoyeom"

ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "app.jar"]