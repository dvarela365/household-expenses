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

    @Column(nullable = false)
    private boolean archived;

    @Column(name = "household_id")
    private Long householdId;

    protected Category() {
        // required by JPA
    }

    public Category(Long householdId, String name, CategoryKind kind, String icon) {
        this.householdId = householdId;
        this.name = name;
        this.kind = kind;
        this.icon = icon;
    }

    /** @deprecated Creates a category without household. Removed in step 1.4 (contract). */
    @Deprecated(forRemoval = true)
    public Category(String name, CategoryKind kind, String icon) {
        this(null, name, kind, icon);
    }

    public void update(String name, CategoryKind kind, String icon) {
        this.name = name;
        this.kind = kind;
        this.icon = icon;
    }

    public boolean isArchived() { return archived; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public CategoryKind getKind() { return kind; }
    public String getIcon() { return icon; }
    public void archive() { this.archived = true; }
    public void unarchive() { this.archived = false; }
    public Long getHouseholdId() { return householdId; }
}
