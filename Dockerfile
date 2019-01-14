FROM alpine:3.8
EXPOSE 9090
ADD /target/DraggerReports-0.3.3.jar /opt/app/
WORKDIR /opt/app
RUN chmod -R 775 /app
CMD java $JAVA_OPTIONS -jar ./*.jar
