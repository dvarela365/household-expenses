# 0004 - Integration tests against real PostgreSQL

**Status:** Accepted

## Context

Repository tests can run against an in-memory database such as H2, which is fast but has a different SQL dialect, different types and different constraint behavior.
Several business rules live in the database itself (UNIQUE, CHECK, FOREIGN KEY).

## Decision

- Repository and integration tests use PostgreSQL 17 in Docker via Testcontainers, wired with `@ServiceConnection`.
- Data tests use `@DataJpaTest` with `@AutoConfigureTestDatabase(replace = NONE)`.
- Every test run applies all Flyway migrations to a fresh database.

## Consequences

- Tests exercise the same database, migrations and constraints as production.
- Tests require Docker and add a few seconds per Spring test context.
- Tests stay independent: `@DataJpaTest` rolls back each test, and JUnit does not guarantee execution order.
