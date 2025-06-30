FROM eclipse-temurin:17-jdk-alpine
WORKDIR /apigateway
COPY target/apigateway.jar apigateway.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
