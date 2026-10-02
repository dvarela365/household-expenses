package com.dvarela.expenses.expense;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateExpenseRequest(
        @NotNull @Positive @Digits(integer = 17, fraction = 2) BigDecimal amount,
        @NotNull LocalDate date,
        @NotNull Long categoryId,
        @NotNull PaymentMethod paymentMethod,
        @Size(max = 80) String merchant,
        @Size(max = 255) String note) {
}