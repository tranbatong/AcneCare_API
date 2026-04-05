# maven:3.9.14-*-noble + eclipse-temurin:21-jre-noble: bản OS/JDK mới hơn jammy, ít CVE đã biết hơn 3.9.9-jammy.
FROM maven:3.9.14-eclipse-temurin-21-noble AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:go-offline -DskipTests

COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre-noble
WORKDIR /app

RUN apt-get update \
    && apt-get upgrade -y --no-install-recommends \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /app/uploads

COPY --from=build /app/target/*.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
