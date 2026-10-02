package com.dvarela.expenses.expense;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.dvarela.expenses.category.Category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;


@Entity
@Table(name = "expense")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "expense_date", nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Column(length = 80)
    private String merchant;

    @Column(length = 255)
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    private Long version;



    protected Expense() {
        // required by JPA
    }

    public Expense(BigDecimal amount, LocalDate date, Category category,
                   PaymentMethod paymentMethod, String merchant, String note) {
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.paymentMethod = paymentMethod;
        this.merchant = merchant;
        this.note = note;
        this.createdAt = Instant.now();
    }

    // ... método de negocio:
    public void update(BigDecimal amount, LocalDate date, Category category,
                       PaymentMethod paymentMethod, String merchant, String note) {
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.paymentMethod = paymentMethod;
        this.merchant = merchant;
        this.note = note;
    }

    public Long getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public Category getCategory() { return category; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public String getMerchant() { return merchant; }
    public String getNote() { return note; }
    public Instant getCreatedAt() { return createdAt; }
    public Long getVersion() { return version; }
}