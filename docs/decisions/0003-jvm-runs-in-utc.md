# 0003 - The JVM runs in UTC

**Status:** Accepted

## Context

On first startup on Windows, PostgreSQL rejected the connection:
`FATAL: invalid value for parameter "TimeZone": "America/Buenos_Aires"`.

The PostgreSQL JDBC driver sends the JVM default time zone when connecting.
Java on Windows maps the local zone to the legacy alias `America/Buenos_Aires`, which the `postgres:17` image (recent Debian) no longer ships.

## Decision

The JVM runs with `-Duser.timezone=UTC`, configured in `pom.xml` for both `spring-boot-maven-plugin` (application) and `maven-surefire-plugin` (tests).

## Consequences

- The application behaves the same on any developer machine, in CI and in cloud containers, which run in UTC by default.
- The server stores and processes moments as UTC instants. Converting to local time is a presentation concern for the frontend.
- Running the application or tests from the IDE requires the same VM option in the run configuration.
