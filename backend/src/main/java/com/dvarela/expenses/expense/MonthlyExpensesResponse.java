package com.dvarela.expenses.expense;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public record MonthlyExpensesResponse(
        YearMonth month,
        BigDecimal total,
        List<ExpenseResponse> expenses) {
}