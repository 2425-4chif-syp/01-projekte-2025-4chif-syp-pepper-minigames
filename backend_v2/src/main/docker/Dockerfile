FROM maven:3-eclipse-temurin-21 AS builder

WORKDIR /opt/pepper

COPY pom.xml .

RUN mvn verify clean --fail-never

COPY . .

RUN mvn -B package

FROM maven:3-eclipse-temurin-21

# copy artifact from build step
WORKDIR /opt/pepper
COPY --from=builder /opt/pepper/target/*-runner.jar /opt/pepper/backend.jar
EXPOSE 8080

ENTRYPOINT [ "java",  "-jar", "backend.jar" ]
