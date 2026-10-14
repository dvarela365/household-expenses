package com.dvarela.expenses.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    private static final Long HOUSEHOLD = 1L;
    private static final Long OTHER_HOUSEHOLD = 2L;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void create_trimsTheNameAndSaves() {
        when(categoryRepository.existsByHouseholdIdAndNameIgnoreCase(HOUSEHOLD, "Mascotas")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResponse response = categoryService.create(HOUSEHOLD,
                new CategoryRequest("  Mascotas  ", CategoryKind.VARIABLE, "dog"));

        assertThat(response.name()).isEqualTo("Mascotas");
    }

    @Test
    void create_rejectsADuplicateName() {
        when(categoryRepository.existsByHouseholdIdAndNameIgnoreCase(HOUSEHOLD, "delivery")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.create(HOUSEHOLD,
                new CategoryRequest("delivery", CategoryKind.VARIABLE, "bike")))
                .isInstanceOf(CategoryNameTakenException.class);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void delete_rejectsACategoryWithExpenses() {
        Category category = new Category(HOUSEHOLD, "Delivery", CategoryKind.VARIABLE, "bike");
        when(categoryRepository.findByIdAndHouseholdId(3L, HOUSEHOLD)).thenReturn(Optional.of(category));
        when(categoryRepository.isInUse(3L)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.delete(HOUSEHOLD, 3L))
                .isInstanceOf(CategoryInUseException.class);
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void delete_removesAnUnusedCategory() {
        Category category = new Category(HOUSEHOLD, "Mascotas", CategoryKind.VARIABLE, "dog");
        when(categoryRepository.findByIdAndHouseholdId(11L, HOUSEHOLD)).thenReturn(Optional.of(category));
        when(categoryRepository.isInUse(11L)).thenReturn(false);

        categoryService.delete(HOUSEHOLD, 11L);

        verify(categoryRepository).delete(category);
    }

    @Test
    void archive_marksTheCategoryAsArchived() {
        Category category = new Category(HOUSEHOLD, "Delivery", CategoryKind.VARIABLE, "bike");
        when(categoryRepository.findByIdAndHouseholdId(3L, HOUSEHOLD)).thenReturn(Optional.of(category));

        CategoryResponse response = categoryService.archive(HOUSEHOLD, 3L);

        assertThat(category.isArchived()).isTrue();
        assertThat(response.archived()).isTrue();
    }

    @Test
    void archive_failsForACategoryOfAnotherHousehold() {
        when(categoryRepository.findByIdAndHouseholdId(3L, OTHER_HOUSEHOLD)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.archive(OTHER_HOUSEHOLD, 3L))
                .isInstanceOf(CategoryNotFoundException.class);
    }
}