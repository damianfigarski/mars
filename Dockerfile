FROM openjdk:10-slim

EXPOSE 8080
WORKDIR /home/docker
COPY ./target/mars-0.0.1-SNAPSHOT.jar /home/docker
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n","-Djava.security.edg=file:/dev/./urandom","-jar","mars-0.0.1-SNAPSHOT.jar"]