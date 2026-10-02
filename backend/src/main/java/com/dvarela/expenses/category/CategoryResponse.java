package com.dvarela.expenses.category;

public record CategoryResponse(Long id, String name, CategoryKind kind, String icon, boolean archived) {

    static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(), category.getName(), category.getKind(),
                category.getIcon(), category.isArchived());
    }
}