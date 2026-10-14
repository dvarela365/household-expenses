package com.dvarela.expenses.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import com.dvarela.expenses.TestcontainersConfiguration;
import com.dvarela.expenses.household.Household;
import com.dvarela.expenses.household.HouseholdRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class CategoryRepositoryTest {

    /** The initial household created by V6; owns the seeded categories. */
    private static final Long INITIAL_HOUSEHOLD = 1L;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private HouseholdRepository householdRepository;

    @Test
    void findAllByHouseholdIdOrderByNameAsc_returnsSeededCategoriesSortedByName() {
        List<Category> categories = categoryRepository.findAllByHouseholdIdOrderByNameAsc(INITIAL_HOUSEHOLD);

        assertThat(categories)
                .extracting(Category::getName)
                .containsExactly(
                        "Café/Kiosco", "Delivery", "Educación", "Hogar", "Ocio",
                        "Otros", "Salud", "Servicios", "Supermercado", "Transporte");
    }

    @Test
    void save_assignsGeneratedId() {
        Category saved = categoryRepository.saveAndFlush(
                new Category(INITIAL_HOUSEHOLD, "Mascotas", CategoryKind.VARIABLE, "paw-print"));

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void save_rejectsDuplicateName() {
        Category duplicate = new Category(INITIAL_HOUSEHOLD, "Delivery", CategoryKind.VARIABLE, "bike");

        assertThatThrownBy(() -> categoryRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void isInUse_isFalseForACategoryWithoutExpenses() {
        Category otros = categoryRepository.findByName("Otros").orElseThrow();

        assertThat(categoryRepository.isInUse(otros.getId())).isFalse();
    }

    @Test
    void save_rejectsADuplicateNameWithDifferentCase() {
        Category duplicate = new Category(INITIAL_HOUSEHOLD, "DELIVERY", CategoryKind.VARIABLE, "bike");

        assertThatThrownBy(() -> categoryRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void findByIdAndHouseholdId_doesNotReturnACategoryOfAnotherHousehold() {
        Household other = householdRepository.save(new Household("Otro hogar"));
        Category foreign = categoryRepository.saveAndFlush(
                new Category(other.getId(), "Mascotas", CategoryKind.VARIABLE, "paw-print"));

        assertThat(categoryRepository.findByIdAndHouseholdId(foreign.getId(), INITIAL_HOUSEHOLD)).isEmpty();
        assertThat(categoryRepository.findAllByHouseholdIdOrderByNameAsc(INITIAL_HOUSEHOLD))
                .extracting(Category::getName)
                .doesNotContain("Mascotas");
    }
}