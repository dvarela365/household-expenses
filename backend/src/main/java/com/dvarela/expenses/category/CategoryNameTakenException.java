package com.dvarela.expenses.category;

public class CategoryNameTakenException extends RuntimeException {

    public CategoryNameTakenException(String name) {
        super("A category named '" + name + "' already exists.");
    }
}