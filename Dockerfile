FROM eclipse-temurin:21-jre-alpine

COPY "./target/*.jar" /loan-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "loan-service.jar"]