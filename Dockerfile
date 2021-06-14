# Build stage

FROM maven:3.8.1-openjdk-11 AS BUILD_STAGE
WORKDIR /code-fit-api
COPY . .
RUN ["mvn", "clean", "install", "-DskipTests"]

# Run stage
FROM openjdk:11.0.6-jre-slim
WORKDIR /code-fit-api

USER root

COPY --from=BUILD_STAGE /code-fit-api/target/*.jar server.jar

RUN apt update && apt install -y docker.io

ADD device/compiler/c_compiler/tmp device/compiler/c_compiler/tmp
ADD device/compiler/java_compiler/tmp  device/compiler/java_compiler/tmp

ADD launch.sh launch.sh

RUN chmod a+x launch.sh

EXPOSE 8082

ENTRYPOINT ["./launch.sh"]