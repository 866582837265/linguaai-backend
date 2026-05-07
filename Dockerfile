FROM eclipse-temurin:17

WORKDIR /app

COPY . .

RUN chmod +x mvnw || true

RUN ./mvnw clean install || mvn clean install

EXPOSE 8080

CMD ["java", "-jar", "target/*.jar"]