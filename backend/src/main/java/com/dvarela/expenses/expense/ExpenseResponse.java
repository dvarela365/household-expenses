package com.dvarela.expenses.expense;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(
        Long id,
        BigDecimal amount,
        LocalDate date,
        Long categoryId,
        String categoryName,
        String categoryIcon,
        PaymentMethod paymentMethod,
        String merchant,
        String note,
        Long version) {

    static ExpenseResponse from(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getAmount(),
                expense.getDate(),
                expense.getCategory().getId(),
                expense.getCategory().getName(),
                expense.getCategory().getIcon(),
                expense.getPaymentMethod(),
                expense.getMerchant(),
                expense.getNote(),
                expense.getVersion());
    }
}