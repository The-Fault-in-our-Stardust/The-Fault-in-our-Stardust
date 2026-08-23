## Running with Docker

From the `java/` directory:

```bash
docker compose up --build
```

The app will be available at `http://localhost:5000`.

By default this runs against SQLite with no additional setup required — the database file persists in `./data/Stardust.db` across container rebuilds via a mounted volume.

### Switching databases (Postgres/MySQL)

The project ships with Postgres and MySQL drivers available but not active by default. To use one instead of SQLite:

1. Set the following environment variables in a `.env` file in `java/`:
SPRING_DATASOURCE_URL=<jdbc-url-for-your-db>
SPRING_DATASOURCE_DRIVER=<driver-class-name>
SPRING_JPA_DIALECT=<hibernate-dialect>
2. Add a `db` service to `docker-compose.yml` (Postgres or MySQL image) once the team finalizes which engine to use.
3. Rebuild: `docker compose up --build`

No code changes are required to switch — `application.properties` reads all datasource config from environment variables with SQLite as the fallback default.

### Notes

- Container runs as a non-root user (`spring`).
- `mvn spring-boot:run` and `docker compose up` both bind port 5000 — stop one before starting the other.

# Stardust (Java Port)

Stardust is a minimal discussion forum built with Java and Spring Boot.

This application was ported from an earlier Python/Flask project called CircusCircus. The goal of this repo is to keep the original forum concept while implementing it using a standard Spring stack (MVC, templates, data repositories, and security).

## What It Includes

- user accounts and authentication
- subforums and posts
- comments on posts
- server-rendered pages using templates
- starter data initialization on first run

## Tech Stack

- Java 17+
- Spring Boot
- Spring MVC + Thymeleaf templates
- Spring Data JPA
- Spring Security
- Maven

## Running the App

From the `java/` directory:

```bash
mvn spring-boot:run
```

Or build and run the jar:

```bash
mvn clean package
java -jar target/Stardust-0.0.1-SNAPSHOT.jar
```

By default, the app runs on:

`http://localhost:8080`

## Notes on the Port

- The original Python project structure and setup instructions no longer apply in this Java module.
- Data and configuration are managed through Spring Boot settings in `src/main/resources/application.properties`.
- Startup data (such as default subforums) is initialized in the Java configuration layer.

## Suggested Next Improvements

- richer post formatting (markdown support)
- reactions (like/dislike)
- direct messaging
- user profile/settings pages
- media embedding (image/video links)
- moderation tools and role-based controls
