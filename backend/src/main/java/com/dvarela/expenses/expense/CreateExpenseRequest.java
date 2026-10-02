package com.dvarela.expenses.expense;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateExpenseRequest(
        BigDecimal amount,
        LocalDate date,
        Long categoryId,
        PaymentMethod paymentMethod,
        String merchant,
        String note) {
}