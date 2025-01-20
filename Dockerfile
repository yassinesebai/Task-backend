FROM openjdk:17
COPY ./target/*.war app.war
EXPOSE 9195
CMD ["java", "-jar", "/app.war"]