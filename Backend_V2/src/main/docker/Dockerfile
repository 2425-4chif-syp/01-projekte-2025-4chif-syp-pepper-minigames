FROM maven:3-eclipse-temurin-21 AS builder

WORKDIR /opt/pepper

COPY pom.xml .

RUN mvn verify clean --fail-never

COPY . .

RUN mvn -B package


# copy artifact from build step
EXPOSE 8080

ENTRYPOINT [ "java",  "-jar", "server.jar" ]
