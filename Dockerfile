FROM eclipse-temurin:21-jdk-alpine
WORKDIR /apigateway
COPY target/apigateway.jar apigateway.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
