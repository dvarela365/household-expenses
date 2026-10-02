package com.dvarela.expenses.expense;

public class ExpenseConflictException extends RuntimeException {

    public ExpenseConflictException(Long id) {
        super("Expense " + id + " was modified by someone else. Reload it and try again.");
    }
}