FROM gradle:8.7-jdk17-alpine AS builder
WORKDIR /app
COPY --chown=gradle:gradle . .

RUN --mount=type=secret,id=gradle_properties,target=/home/gradle/.gradle/gradle.properties \
    gradle clean bootJar --no-daemon --info

FROM eclipse-temurin:17-jre-alpine
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]