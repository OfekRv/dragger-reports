FROM openjdk:8-jre-alpine
RUN mvn package
EXPOSE 8081
