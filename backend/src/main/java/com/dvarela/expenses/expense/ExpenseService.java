package com.dvarela.expenses.expense;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dvarela.expenses.category.Category;
import com.dvarela.expenses.category.CategoryNotFoundException;
import com.dvarela.expenses.category.CategoryRepository;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public ExpenseService(ExpenseRepository expenseRepository,
                          CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public ExpenseResponse register(CreateExpenseRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        Expense expense = new Expense(request.amount(), request.date(), category,
                request.paymentMethod(), request.merchant(), request.note());

        return ExpenseResponse.from(expenseRepository.save(expense));
    }

    @Transactional(readOnly = true)
    public MonthlyExpensesResponse findByMonth(YearMonth month) {
        List<Expense> expenses = expenseRepository.findAllInPeriodWithCategory(
                month.atDay(1), month.plusMonths(1).atDay(1));

        BigDecimal total = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<ExpenseResponse> items = expenses.stream()
                .map(ExpenseResponse::from)
                .toList();

        return new MonthlyExpensesResponse(month, total, items);
    }
}