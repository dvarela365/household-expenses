# 0002 - Flyway owns the schema; Hibernate validates

**Status:** Accepted

## Context

Hibernate can create or alter tables from entity mappings (`ddl-auto=create` or `update`).
That is convenient early on but unsafe with real data: changes are implicit, unreviewed and not reproducible across environments.

## Decision

- Every schema change is a versioned SQL migration managed by Flyway (`V<n>__description.sql`).
- Applied migrations are never edited. Changes go in a new migration.
- Hibernate runs with `spring.jpa.hibernate.ddl-auto=validate`.
- Constraints and indexes are named explicitly (`fk_`, `uk_`, `ck_`, `ix_`).

## Consequences

- The schema history is explicit, reviewable and identical in every environment.
- If an entity and its table drift apart, the application fails at startup instead of at query time. This was verified by renaming the mapped table and observing the startup failure.
- Flyway runs before Hibernate initializes, so Hibernate never sees an outdated schema.
