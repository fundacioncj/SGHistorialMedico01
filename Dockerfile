# Dockerfile (Java)
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends maven && rm -rf /var/lib/apt/lists/*
COPY pom.xml .
RUN mvn -q -B -e -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

FROM eclipse-temurin:17-jre
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV TZ=UTC
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/target/*.jar app.jar
ENV SPRING_CONFIG_ADDITIONAL_LOCATION="optional:file:/app/config/"
EXPOSE 8080
ENV SERVER_PORT=8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dserver.port=${SERVER_PORT} -jar app.jar"]
