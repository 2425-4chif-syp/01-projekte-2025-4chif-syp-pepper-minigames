FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Kopiere das erstellte JAR in das Container-Verzeichnis
COPY target/backend-1.0.0-SNAPSHOT-runner.jar /app/app.jar

# Setze den Startbefehl für die Java-Anwendung
CMD ["java", "-jar", "/app/app.jar"]

# Der Container lauscht auf Port 8080
EXPOSE 8080
