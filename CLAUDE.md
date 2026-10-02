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

## Frontend (`frontend/`)

- React, TypeScript, Vite and Tailwind CSS v4. Mobile-first: design for a phone screen first.
- Commands, from `frontend/`: `npm run dev`, `npm run lint`, `npm run build`.
  A frontend task is done only when `npm run lint` and `npm run build` pass.
- Call the backend with relative URLs under `/api`. Vite proxies them to port 8080. Never hardcode `localhost:8080`.
- API types mirror the backend DTOs and live in `src/api/`, together with the functions that call the API.
- One component per file in `src/components/`.
- User-facing text in Spanish (Argentina). Format money as ARS with the `es-AR` locale.
- Do not display backend validation messages verbatim. Use the field names in the `errors` object to show Spanish messages.
- Do not add npm dependencies without asking first.

## Verification limits

- Do not start, stop or kill processes. Ask the user to run the app and report back.
- Do not install packages, not even temporarily.
- Do not write to the development database. Never create test data there.
- Do not create files outside the scope of the task, not even temporary ones.