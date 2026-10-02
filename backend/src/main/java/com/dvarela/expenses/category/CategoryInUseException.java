package com.dvarela.expenses.category;

public class CategoryInUseException extends RuntimeException {

    public CategoryInUseException(Long id) {
        super("Category " + id + " has expenses. Archive it instead of deleting it.");
    }
}