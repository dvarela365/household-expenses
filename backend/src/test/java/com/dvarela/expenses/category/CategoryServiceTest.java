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

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void create_trimsTheNameAndSaves() {
        when(categoryRepository.existsByNameIgnoreCase("Mascotas")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResponse response = categoryService.create(
                new CategoryRequest("  Mascotas  ", CategoryKind.VARIABLE, "dog"));

        assertThat(response.name()).isEqualTo("Mascotas");
    }

    @Test
    void create_rejectsADuplicateName() {
        when(categoryRepository.existsByNameIgnoreCase("delivery")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.create(
                new CategoryRequest("delivery", CategoryKind.VARIABLE, "bike")))
                .isInstanceOf(CategoryNameTakenException.class);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void delete_rejectsACategoryWithExpenses() {
        Category category = new Category("Delivery", CategoryKind.VARIABLE, "bike");
        when(categoryRepository.findById(3L)).thenReturn(Optional.of(category));
        when(categoryRepository.isInUse(3L)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.delete(3L))
                .isInstanceOf(CategoryInUseException.class);
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void delete_removesAnUnusedCategory() {
        Category category = new Category("Mascotas", CategoryKind.VARIABLE, "dog");
        when(categoryRepository.findById(11L)).thenReturn(Optional.of(category));
        when(categoryRepository.isInUse(11L)).thenReturn(false);

        categoryService.delete(11L);

        verify(categoryRepository).delete(category);
    }

    @Test
    void archive_marksTheCategoryAsArchived() {
        Category category = new Category("Delivery", CategoryKind.VARIABLE, "bike");
        when(categoryRepository.findById(3L)).thenReturn(Optional.of(category));

        CategoryResponse response = categoryService.archive(3L);

        assertThat(category.isArchived()).isTrue();
        assertThat(response.archived()).isTrue();
    }
}