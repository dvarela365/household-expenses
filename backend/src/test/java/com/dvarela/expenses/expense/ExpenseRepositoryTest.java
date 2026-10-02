package com.dvarela.expenses.expense;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import com.dvarela.expenses.TestcontainersConfiguration;
import com.dvarela.expenses.category.Category;
import com.dvarela.expenses.category.CategoryRepository;

import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    private Category supermercado;

    @BeforeEach
    void setUp() {
        supermercado = categoryRepository.findByName("Supermercado").orElseThrow();
    }

    @Test
    void findAllInPeriodWithCategory_returnsOnlyExpensesInPeriodNewestFirst() {
        save("100.00", LocalDate.of(2026, 9, 30));
        save("200.00", LocalDate.of(2026, 10, 1));
        save("300.00", LocalDate.of(2026, 10, 31));
        save("400.00", LocalDate.of(2026, 11, 1));
        entityManager.clear();

        List<Expense> result = expenseRepository.findAllInPeriodWithCategory(
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 11, 1));

        assertThat(result)
                .extracting(Expense::getDate)
                .containsExactly(LocalDate.of(2026, 10, 31), LocalDate.of(2026, 10, 1));
        assertThat(result)
                .allSatisfy(e -> assertThat(Hibernate.isInitialized(e.getCategory())).isTrue());
    }

    @Test
    void amount_keepsExactDecimalValue() {
        Expense saved = save("1234.5", LocalDate.of(2026, 10, 1));
        entityManager.clear();

        Expense found = expenseRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getAmount()).isEqualByComparingTo("1234.5");
        assertThat(found.getAmount()).isEqualTo(new BigDecimal("1234.50"));
    }

    @Test
    void save_rejectsNonPositiveAmount() {
        Expense invalid = new Expense(BigDecimal.ZERO, LocalDate.of(2026, 10, 1),
                supermercado, PaymentMethod.CASH, null, null);

        assertThatThrownBy(() -> expenseRepository.saveAndFlush(invalid))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private Expense save(String amount, LocalDate date) {
        return expenseRepository.saveAndFlush(new Expense(
                new BigDecimal(amount), date, supermercado, PaymentMethod.DEBIT, null, null));
    }
}