FROM openjdk:latest
COPY --from=target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]