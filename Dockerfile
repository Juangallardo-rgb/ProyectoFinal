FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/inventory-app.jar inventory-app.jar

EXPOSE 8080

CMD ["java", "--add-modules", "jdk.httpserver", "-jar", "inventory-app.jar"]
