package com.dvarela.expenses.expense;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;

/** Half-open date range [start, endExclusive) covering one calendar month. */
record MonthRange(LocalDate start, LocalDate endExclusive) {

    MonthRange {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(endExclusive, "endExclusive");
        if (!start.isBefore(endExclusive)) {
            throw new IllegalArgumentException("start must be before endExclusive");
        }
    }

    static MonthRange of(YearMonth month) {
        return new MonthRange(month.atDay(1), month.plusMonths(1).atDay(1));
    }
}