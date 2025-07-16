FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/account_service.jar app.jar
ENV ACCOUNT_PORT=8080
EXPOSE ${ACCOUNT_PORT}
ENTRYPOINT ["java","-jar","app.jar"]
