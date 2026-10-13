package com.dvarela.expenses.expense;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.YearMonth;

import org.junit.jupiter.api.Test;

class MonthRangeTest {

    @Test
    void of_coversFromTheFirstDayToTheFirstDayOfNextMonth() {
        MonthRange range = MonthRange.of(YearMonth.of(2026, 10));

        assertThat(range.start()).isEqualTo(LocalDate.of(2026, 10, 1));
        assertThat(range.endExclusive()).isEqualTo(LocalDate.of(2026, 11, 1));
    }

    @Test
    void of_crossesTheYearInDecember() {
        MonthRange range = MonthRange.of(YearMonth.of(2026, 12));

        assertThat(range.endExclusive()).isEqualTo(LocalDate.of(2027, 1, 1));
    }

    @Test
    void rejectsAStartThatIsNotBeforeTheEnd() {
        LocalDate day = LocalDate.of(2026, 10, 1);

        assertThatThrownBy(() -> new MonthRange(day, day))
                .isInstanceOf(IllegalArgumentException.class);
    }
}