FROM openjdk:17
WORKDIR /app
COPY ./target/ola-web-backend.war /app
CMD ["java", "-jar", "ola-web-backend.war"]