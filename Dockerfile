FROM gradle:7.4.2-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle assemble

FROM openjdk:11
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/SimpleBlockchain.jar /app/SimpleBlockchain.jar