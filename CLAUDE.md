# CLAUDE.md

Guidance for AI coding agents working in this repository.

## Project

Household expense tracker. Monorepo: `backend/` (Spring Boot) and, later, `frontend/` (React + TypeScript).
Read `README.md` for the roadmap and `docs/decisions/` for the reasoning behind the conventions below.

## Commands

Run from `backend/`:

- `./mvnw spring-boot:run` - start the app (Docker Compose starts PostgreSQL)
- `./mvnw test` - run all tests (requires Docker running)

Run Git commands from the repository root.

## Conventions

- Java 21, Spring Boot 4.1. Code, identifiers and commit messages in English. User-facing data (category names) in Spanish.
- Package by feature: `com.dvarela.expenses.<feature>`.
- Conventional Commits: `feat:`, `fix:`, `test:`, `docs:`, `chore:`, `refactor:`.
- No Lombok. DTOs are `record`s. Entities: protected no-arg constructor, full constructor, getters, no public setters.
- Constructor injection in production code.

## Database rules

- Flyway owns the schema. Hibernate runs with `ddl-auto=validate`.
- Never edit a migration that has been applied. Add a new `V<n>__description.sql`.
- Name every constraint: `pk_`, `fk_`, `uk_`, `ck_`, and `ix_` for indexes.
- Money: `NUMERIC(19,2)` / `BigDecimal`. Compare amounts with `compareTo`, never `equals`.
- Dates: `LocalDate` for calendar dates, `Instant` / `TIMESTAMPTZ` for moments. The JVM runs in UTC.
- All associations are `LAZY`. Load what a use case needs with `join fetch`. Open Session In View is disabled.

## Testing rules

- Every change ships with tests.
- Repository tests: `@DataJpaTest` + `@AutoConfigureTestDatabase(replace = NONE)` + `@Import(TestcontainersConfiguration.class)`.
- Never use H2 or another in-memory database.
- Use `saveAndFlush` when a test depends on a database constraint, and `entityManager.clear()` before reading back.

## Do not

- Do not change `ddl-auto`, the Flyway migrations already applied, or the UTC settings in `pom.xml`.
- Do not add dependencies without explaining why.
- Do not commit or push. Leave changes for human review.
