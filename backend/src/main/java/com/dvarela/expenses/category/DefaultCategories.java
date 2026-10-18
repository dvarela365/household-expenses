package com.dvarela.expenses.category;

import java.util.List;

/** The categories every new household starts with (same set as the V2 seed). */
public final class DefaultCategories {

    private DefaultCategories() {
    }

    public static List<Category> forHousehold(Long householdId) {
        return List.of(
                new Category(householdId, "Supermercado", CategoryKind.VARIABLE, "shopping-cart"),
                new Category(householdId, "Servicios", CategoryKind.FIXED, "zap"),
                new Category(householdId, "Delivery", CategoryKind.VARIABLE, "bike"),
                new Category(householdId, "Café/Kiosco", CategoryKind.VARIABLE, "coffee"),
                new Category(householdId, "Transporte", CategoryKind.VARIABLE, "bus"),
                new Category(householdId, "Salud", CategoryKind.VARIABLE, "heart-pulse"),
                new Category(householdId, "Educación", CategoryKind.FIXED, "graduation-cap"),
                new Category(householdId, "Hogar", CategoryKind.VARIABLE, "home"),
                new Category(householdId, "Ocio", CategoryKind.VARIABLE, "popcorn"),
                new Category(householdId, "Otros", CategoryKind.VARIABLE, "circle"));
                new Category(householdId, "Salud", CategoryKind.FIXED, "heart-pulse"),
    }
}