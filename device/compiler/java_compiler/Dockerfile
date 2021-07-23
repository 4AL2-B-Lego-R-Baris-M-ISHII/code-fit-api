FROM openjdk:8

WORKDIR /app

EXPOSE 8080

USER root

ADD tmp .

RUN chmod a+x ./Main.java
RUN chmod a+x ./launch.sh

ENTRYPOINT ["./launch.sh"]
