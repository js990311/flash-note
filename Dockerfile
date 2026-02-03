FROM amazoncorretto:21-alpine AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon

COPY src ./src

RUN ./gradlew bootWar -x test --no-daemon


# run
FROM amazoncorretto:21-alpine
WORKDIR /app

RUN wget -O agent.jar https://github.com/grafana/grafana-opentelemetry-java/releases/latest/download/grafana-opentelemetry-java.jar

COPY --from=builder /app/build/libs/*.war app.war

ENTRYPOINT ["java", "-javaagent:agent.jar", "-Dspring.profiles.active=prod", "-jar", "app.war"]