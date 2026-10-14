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
import com.dvarela.expenses.category.CategoryKind;
import com.dvarela.expenses.category.CategoryRepository;
import com.dvarela.expenses.household.Household;
import com.dvarela.expenses.household.HouseholdRepository;

import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class ExpenseRepositoryTest {

    /** The initial household created by V6; owns the seeded categories. */
    private static final Long INITIAL_HOUSEHOLD = 1L;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private HouseholdRepository householdRepository;

    @Autowired
    private EntityManager entityManager;

    private Category supermercado;

    @BeforeEach
    void setUp() {
        supermercado = seededCategory("Supermercado");
    }

    @Test
    void findAllInPeriodWithCategory_returnsOnlyExpensesInPeriodNewestFirst() {
        save(INITIAL_HOUSEHOLD, supermercado, "100.00", LocalDate.of(2026, 9, 30));
        save(INITIAL_HOUSEHOLD, supermercado, "200.00", LocalDate.of(2026, 10, 1));
        save(INITIAL_HOUSEHOLD, supermercado, "300.00", LocalDate.of(2026, 10, 31));
        save(INITIAL_HOUSEHOLD, supermercado, "400.00", LocalDate.of(2026, 11, 1));
        entityManager.clear();

        List<Expense> result = expenseRepository.findAllInPeriodWithCategory(
                INITIAL_HOUSEHOLD, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 11, 1));

        assertThat(result)
                .extracting(Expense::getDate)
                .containsExactly(LocalDate.of(2026, 10, 31), LocalDate.of(2026, 10, 1));
        assertThat(result)
                .allSatisfy(e -> assertThat(Hibernate.isInitialized(e.getCategory())).isTrue());
    }

    @Test
    void findAllInPeriodWithCategory_excludesExpensesOfAnotherHousehold() {
        Household other = householdRepository.save(new Household("Otro hogar"));
        Category otherCategory = categoryRepository.saveAndFlush(
                new Category(other.getId(), "Varios", CategoryKind.VARIABLE, "package"));
        save(INITIAL_HOUSEHOLD, supermercado, "100.00", LocalDate.of(2026, 10, 10));
        save(other.getId(), otherCategory, "999.00", LocalDate.of(2026, 10, 10));
        entityManager.clear();

        List<Expense> result = expenseRepository.findAllInPeriodWithCategory(
                INITIAL_HOUSEHOLD, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 11, 1));

        assertThat(result)
                .extracting(Expense::getAmount)
                .usingElementComparator(BigDecimal::compareTo)
                .containsExactly(new BigDecimal("100.00"));
    }

    @Test
    void findByIdAndHouseholdId_doesNotReturnAnExpenseOfAnotherHousehold() {
        Expense mine = save(INITIAL_HOUSEHOLD, supermercado, "100.00", LocalDate.of(2026, 10, 1));
        entityManager.clear();

        assertThat(expenseRepository.findByIdAndHouseholdId(mine.getId(), INITIAL_HOUSEHOLD)).isPresent();
        assertThat(expenseRepository.findByIdAndHouseholdId(mine.getId(), 999L)).isEmpty();
    }

    @Test
    void amount_keepsExactDecimalValue() {
        Expense saved = save(INITIAL_HOUSEHOLD, supermercado, "1234.5", LocalDate.of(2026, 10, 1));
        entityManager.clear();

        Expense found = expenseRepository.findByIdAndHouseholdId(saved.getId(), INITIAL_HOUSEHOLD).orElseThrow();

        assertThat(found.getAmount()).isEqualByComparingTo("1234.5");
        assertThat(found.getAmount()).isEqualTo(new BigDecimal("1234.50"));
    }

    @Test
    void save_rejectsNonPositiveAmount() {
        Expense invalid = new Expense(INITIAL_HOUSEHOLD, BigDecimal.ZERO, LocalDate.of(2026, 10, 1),
                supermercado, PaymentMethod.CASH, null, null);

        assertThatThrownBy(() -> {
            expenseRepository.save(invalid);
            expenseRepository.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    private Expense save(Long householdId, Category category, String amount, LocalDate date) {
        Expense saved = expenseRepository.save(new Expense(
                householdId, new BigDecimal(amount), date, category, PaymentMethod.DEBIT, null, null));
        expenseRepository.flush();
        return saved;
    }

    private Category seededCategory(String name) {
        return categoryRepository.findAllByHouseholdIdOrderByNameAsc(INITIAL_HOUSEHOLD).stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }
}