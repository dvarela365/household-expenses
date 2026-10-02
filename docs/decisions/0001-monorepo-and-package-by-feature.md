# 0001 - Monorepo and package by feature

**Status:** Accepted

## Context

The application has a Spring Boot backend and will have a React frontend that is deployed together with it.
Inside the backend, code can be grouped by technical layer (controllers, services, repositories) or by feature.

## Decision

- One repository with `backend/` and `frontend/` folders.
- Backend code is grouped by feature: `com.dvarela.expenses.category`, `com.dvarela.expenses.expense`, and so on.

## Consequences

- One history, one CI pipeline, and changes that touch both sides land in a single commit.
- Code that changes together lives together. Each feature package can later become a module, which eases a move to hexagonal architecture.
- Maven commands run from `backend/`; Git commands run from the repository root.
