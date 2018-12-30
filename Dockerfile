FROM openjdk:8-jdk-alpine
EXPOSE 9090
COPY ./target/*.jar /opt/app/
WORKDIR /opt/app
RUN chmod -R 775 /opt/app
CMD java $JAVA_OPTIONS -jar ./*.jar
