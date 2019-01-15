FROM openjdk:8-jre-alpine
EXPOSE 9090
ls -l
ADD ./target/*.jar /opt/app/
WORKDIR /opt/app
RUN chmod -R 775 /app
CMD java $JAVA_OPTIONS -jar ./*.jar
