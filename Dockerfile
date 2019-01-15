FROM openjdk:8-jdk-alpine
EXPOSE 9090
WORKDIR /app
RUN chmod -R 775 /app
CMD java $JAVA_OPTIONS -jar ./*.jar
