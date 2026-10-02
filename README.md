# household-expenses

A household expense tracker: record expenses in seconds, track recurring bills and due dates, and see month-over-month spending by category.

Built step by step as a full-stack learning project.

## Stack

- **Backend:** Java 21, Spring Boot 4.1, Spring Data JPA (Hibernate 7), Flyway
- **Database:** PostgreSQL 17
- **Testing:** JUnit 5, AssertJ, Testcontainers (real PostgreSQL, no H2)
- **Local infrastructure:** Docker Compose (started automatically by Spring Boot)
- **Frontend (planned):** React, TypeScript, Vite, Tailwind CSS, as an installable PWA

## Running locally

Requirements: JDK 21 and Docker Desktop running.

```bash
cd backend
./mvnw spring-boot:run   # starts PostgreSQL via Docker Compose, then the app on :8080
./mvnw test              # runs all tests against PostgreSQL in Testcontainers
```

## Project structure

```
backend/
  src/main/java/com/dvarela/expenses/
    category/     # Category entity and repository
    expense/      # Expense entity and repository
  src/main/resources/db/migration/   # Flyway migrations (never edit an applied one)
docs/decisions/   # Architecture Decision Records
```

Code is organized by feature, not by layer.

## Roadmap

- [x] **Phase 0** - Project skeleton, PostgreSQL in Docker, UTC, Git
- [ ] **Slice 1 - MVP:** record an expense and see the current month
  - [x] Category: migrations, entity, repository, integration tests
  - [x] Expense: migration, entity, period query, integration tests
  - [x] Service layer with unit tests (Mockito)
  - [x] REST API with validation and error handling (MockMvc)
  - [x] Frontend: quick-entry screen and monthly list
- [ ] **Slice 2 - In the cloud:** login, Dockerfile, CI, cloud deployment
- [ ] **Slice 3 - Due dates:** recurring bills, due date tracking, email reminders
- [ ] **Slice 4 - Control:** budgets with traffic-light status, month-over-month KPIs
- [ ] **Slice 5 - Shared household:** two users on the same data
- [ ] **Slice 6 - Optimization:** small recurring expense detection and saving suggestions

## Architecture decisions

See [`docs/decisions`](docs/decisions).
