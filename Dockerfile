FROM gradle:8.14-jdk24 AS backend_build
WORKDIR /meet-map
COPY . .
RUN gradle assemble

FROM eclipse-temurin:24-jre
WORKDIR /meet-map
COPY --from=backend_build /meet-map/backend/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
