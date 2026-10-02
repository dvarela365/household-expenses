# Architecture Decision Records

Short records of significant decisions: the context, what was decided, and the consequences.
A decision is never edited after it is accepted; if it changes, a new record supersedes it.

| # | Decision | Status |
|---|----------|--------|
| [0001](0001-monorepo-and-package-by-feature.md) | Monorepo and package by feature | Accepted |
| [0002](0002-flyway-owns-schema-hibernate-validates.md) | Flyway owns the schema; Hibernate validates | Accepted |
| [0003](0003-jvm-runs-in-utc.md) | The JVM runs in UTC | Accepted |
| [0004](0004-testcontainers-instead-of-h2.md) | Integration tests against real PostgreSQL | Accepted |
| [0005](0005-money-as-numeric-and-bigdecimal.md) | Money as NUMERIC and BigDecimal | Accepted |
| [0006](0006-lazy-associations-and-no-osiv.md) | Lazy associations, explicit fetch joins, no OSIV | Accepted |
