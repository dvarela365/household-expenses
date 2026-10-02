package com.dvarela.expenses.expense;

public class UnknownCategoryException extends RuntimeException {

    public UnknownCategoryException(Long id) {
        super("Category not found: " + id);
    }
}