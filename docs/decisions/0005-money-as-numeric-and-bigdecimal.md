# 0005 - Money as NUMERIC and BigDecimal

**Status:** Accepted

## Context

Binary floating-point types (`double`, `FLOAT`) cannot represent values such as 0.10 exactly, so sums drift by fractions of a cent.

## Decision

- Amounts are stored as `NUMERIC(19,2)` and mapped to `BigDecimal`.
- Amounts are compared with `compareTo` (in tests, AssertJ `isEqualByComparingTo`), never with `equals`.
- Division, when needed, always specifies scale and `RoundingMode`.
- A `CHECK (amount > 0)` constraint guards the data even if application validation fails.

## Consequences

- Totals are exact.
- `BigDecimal.equals` compares scale as well as value (`1234.5` is not equal to `1234.50`), so the comparison rule must be followed consistently.
