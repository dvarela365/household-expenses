# 0006 - Lazy associations, explicit fetch joins, no Open Session In View

**Status:** Accepted

## Context

JPA loads `@ManyToOne` associations eagerly by default. Spring Boot enables Open Session In View (OSIV) by default, which keeps the persistence context and database connection open for the whole HTTP request.
Together they hide N+1 query problems and hold pool connections longer than needed.

## Decision

- Every association is declared `LAZY`.
- Each use case loads what it needs explicitly, with `join fetch` in JPQL.
- `spring.jpa.open-in-view=false`.
- Associations are unidirectional unless there is a concrete need: `Expense` references `Category`; `Category` has no collection of expenses.

## Consequences

- Queries are explicit and predictable. The monthly expense list loads expenses and categories in a single SQL statement.
- Accessing an unloaded association outside a transaction fails with `LazyInitializationException`. That is treated as a signal to fetch the data in the service, not as a reason to re-enable OSIV.
