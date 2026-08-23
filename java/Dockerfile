# --- Build stage ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# --- Runtime stage ---
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN groupadd -r spring && useradd -r -g spring spring
RUN mkdir -p /app/data && chown -R spring:spring /app/data
USER spring:spring
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9201
ENTRYPOINT ["java", "-jar", "app.jar"]

# # --- Runtime stage ---
# FROM eclipse-temurin:17-jre-alpine
# WORKDIR /app
# RUN addgroup -S spring && adduser -S spring -G spring
# USER spring:spring
# COPY --from=build /app/target/*.jar app.jar
# EXPOSE 5000
# ENTRYPOINT ["java", "-jar", "app.jar"]