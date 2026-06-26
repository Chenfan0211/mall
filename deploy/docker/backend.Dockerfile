FROM maven:3.9.9-eclipse-temurin-21 AS build

ARG MODULE
WORKDIR /workspace

COPY . .

RUN --mount=type=cache,target=/root/.m2 mvn -s deploy/maven/settings.xml -pl ${MODULE} -am clean package -Dmaven.test.skip=true

FROM eclipse-temurin:18-jre

ARG MODULE
ENV TZ=Asia/Shanghai \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport"

WORKDIR /app
COPY --from=build /workspace/${MODULE}/target/${MODULE}-*-exec.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
