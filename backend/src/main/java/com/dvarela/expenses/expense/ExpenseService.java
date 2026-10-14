package com.dvarela.expenses.expense;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dvarela.expenses.category.Category;
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
    public ExpenseResponse register(Long householdId, CreateExpenseRequest request) {
        Category category = findCategory(householdId, request.categoryId());

        Expense expense = new Expense(householdId, request.amount(), request.date(), category,
                request.paymentMethod(), request.merchant(), request.note());

        return ExpenseResponse.from(expenseRepository.save(expense));
    }

    @Transactional(readOnly = true)
    public MonthlyExpensesResponse findByMonth(Long householdId, YearMonth month) {
        MonthRange range = MonthRange.of(month);
        List<Expense> expenses = expenseRepository.findAllInPeriodWithCategory(
                householdId, range.start(), range.endExclusive());

        BigDecimal total = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<ExpenseResponse> items = expenses.stream()
                .map(ExpenseResponse::from)
                .toList();

        return new MonthlyExpensesResponse(month, total, items);
    }

    @Transactional
    public ExpenseResponse update(Long householdId, Long id, UpdateExpenseRequest request) {
        Expense expense = findExpense(householdId, id);

        if (!Objects.equals(expense.getVersion(), request.version())) {
            throw new ExpenseConflictException(id);
        }

        Category category = findCategory(householdId, request.categoryId());

        expense.update(request.amount(), request.date(), category,
                request.paymentMethod(), request.merchant(), request.note());

        expenseRepository.flush();
        return ExpenseResponse.from(expense);
    }

    @Transactional
    public void delete(Long householdId, Long id) {
        expenseRepository.delete(findExpense(householdId, id));
    }

    private Expense findExpense(Long householdId, Long id) {
        return expenseRepository.findByIdAndHouseholdId(id, householdId)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
    }

    private Category findCategory(Long householdId, Long categoryId) {
        return categoryRepository.findByIdAndHouseholdId(categoryId, householdId)
                .orElseThrow(() -> new UnknownCategoryException(categoryId));
    }
}