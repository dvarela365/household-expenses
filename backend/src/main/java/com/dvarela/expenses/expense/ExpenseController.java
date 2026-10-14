package com.dvarela.expenses.expense;

import java.time.YearMonth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.dvarela.expenses.household.CurrentHousehold;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final CurrentHousehold currentHousehold;

    public ExpenseController(ExpenseService expenseService, CurrentHousehold currentHousehold) {
        this.expenseService = expenseService;
        this.currentHousehold = currentHousehold;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse register(@Valid @RequestBody CreateExpenseRequest request) {
        return expenseService.register(currentHousehold.id(), request);
    }

    @GetMapping
    public MonthlyExpensesResponse findByMonth(@RequestParam YearMonth month) {
        return expenseService.findByMonth(currentHousehold.id(), month);
    }

    @PutMapping("/{id}")
    public ExpenseResponse update(@PathVariable Long id,
                                  @Valid @RequestBody UpdateExpenseRequest request) {
        return expenseService.update(currentHousehold.id(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        expenseService.delete(currentHousehold.id(), id);
    }
}