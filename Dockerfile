FROM gradle:7.4.2-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle MyFatJar

FROM openjdk:11
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/SimpleBlockchain-1.0.jar /app/SimpleBlockchain-1.0.jar