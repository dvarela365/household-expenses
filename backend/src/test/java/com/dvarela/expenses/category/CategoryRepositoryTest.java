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

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findAllByOrderByNameAsc_returnsSeededCategoriesSortedByName() {
        List<Category> categories = categoryRepository.findAllByOrderByNameAsc();

        assertThat(categories)
                .extracting(Category::getName)
                .containsExactly(
                        "Café/Kiosco", "Delivery", "Educación", "Hogar", "Ocio",
                        "Otros", "Salud", "Servicios", "Supermercado", "Transporte");
    }

    @Test
    void save_assignsGeneratedId() {
        Category saved = categoryRepository.saveAndFlush(
                new Category("Mascotas", CategoryKind.VARIABLE, "paw-print"));

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void save_rejectsDuplicateName() {
        Category duplicate = new Category("Delivery", CategoryKind.VARIABLE, "bike");

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
        Category duplicate = new Category("DELIVERY", CategoryKind.VARIABLE, "bike");

        assertThatThrownBy(() -> categoryRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}