package com.dvarela.expenses.category;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CategoryKind kind;

    @Column(length = 30)
    private String icon;

    protected Category() {
        // required by JPA
    }

    public Category(String name, CategoryKind kind, String icon) {
        this.name = name;
        this.kind = kind;
        this.icon = icon;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public CategoryKind getKind() { return kind; }
    public String getIcon() { return icon; }
}
