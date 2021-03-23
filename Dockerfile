FROM openjdk:latest
COPY API/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]