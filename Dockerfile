# USAGE
# docker build -t auth .
# docker run --rm -it -p 8081:8081 auth
FROM maven:3.5-jdk-8 AS builder
WORKDIR /workspace
COPY . .
RUN mvn clean package -DskipTests

FROM adoptopenjdk/openjdk8:x86_64-alpine-jre8u232-b09
WORKDIR /workspace
# ENV DB_HOST=${DB_HOST}
# ENV DB_NAME=${DB_NAME}
# ENV DB_USER=${DB_USER}
# ENV DB_PASSSWORD=${DB_PASSSWORD}
# ENV AUTH_JWT_PRIVATE_KEY=${AUTH_JWT_PRIVATE_KEY}
# ENV AUTH_JWT_PUBLIC_KEY=${AUTH_JWT_PUBLIC_KEY}
COPY --from=builder /workspace/target/*.jar ./auth.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "auth.jar"]
# ENTRYPOINT [ "java", "-Dspring.profiles.active=prod", "-jar", "auth.jar" ]
