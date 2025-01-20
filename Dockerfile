FROM openjdk:17
WORKDIR /app
COPY ./target/backend.war /app
EXPOSE 9195
CMD ["java", "-jar", "backend.war"]