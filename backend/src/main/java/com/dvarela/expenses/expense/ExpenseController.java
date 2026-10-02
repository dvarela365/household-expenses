package com.dvarela.expenses.expense;

import java.time.YearMonth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse register(@Valid @RequestBody CreateExpenseRequest request) {
        return expenseService.register(request);
    }

    @GetMapping
    public MonthlyExpensesResponse findByMonth(@RequestParam YearMonth month) {
        return expenseService.findByMonth(month);
    }

    @PutMapping("/{id}")
    public ExpenseResponse update(@PathVariable Long id,
                                  @Valid @RequestBody UpdateExpenseRequest request) {
        return expenseService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        expenseService.delete(id);
    }
}