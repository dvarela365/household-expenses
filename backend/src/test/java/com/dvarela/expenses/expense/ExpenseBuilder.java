package com.dvarela.expenses.expense;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.test.util.ReflectionTestUtils;

import com.dvarela.expenses.category.Category;
import com.dvarela.expenses.category.CategoryKind;

final class ExpenseBuilder {

    private BigDecimal amount = new BigDecimal("100.00");
    private LocalDate date = LocalDate.of(2026, 10, 1);
    private Category category = new Category("Supermercado", CategoryKind.VARIABLE, "shopping-cart");
    private PaymentMethod paymentMethod = PaymentMethod.DEBIT;
    private String merchant;
    private String note;
    private Long id;
    private Long version;

    private ExpenseBuilder() {
    }

    static ExpenseBuilder anExpense() {
        return new ExpenseBuilder();
    }

    ExpenseBuilder withAmount(String amount) {
        this.amount = new BigDecimal(amount);
        return this;
    }

    ExpenseBuilder onDate(LocalDate date) {
        this.date = date;
        return this;
    }

    ExpenseBuilder inCategory(Category category) {
        this.category = category;
        return this;
    }

    ExpenseBuilder paidWith(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    ExpenseBuilder atMerchant(String merchant) {
        this.merchant = merchant;
        return this;
    }

    ExpenseBuilder persisted(Long id, Long version) {
        this.id = id;
        this.version = version;
        return this;
    }

    Expense build() {
        Expense expense = new Expense(amount, date, category, paymentMethod, merchant, note);
        if (id != null) {
            ReflectionTestUtils.setField(expense, "id", id);
            ReflectionTestUtils.setField(expense, "version", version);
        }
        return expense;
    }
}