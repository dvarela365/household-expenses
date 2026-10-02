package com.dvarela.expenses.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void findAll_returnsCategoriesAsJson() {
        when(categoryService.findAll(false)).thenReturn(List.of(
                new CategoryResponse(4L, "Café/Kiosco", CategoryKind.VARIABLE, "coffee", false),
                new CategoryResponse(3L, "Delivery", CategoryKind.VARIABLE, "bike", false)));

        assertThat(mvc.get().uri("/api/categories"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$[0].name").isEqualTo("Café/Kiosco");
    }

    @Test
    void create_returns201WithTheNewCategory() {
        when(categoryService.create(any())).thenReturn(
                new CategoryResponse(11L, "Mascotas", CategoryKind.VARIABLE, "dog", false));

        assertThat(mvc.post().uri("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "Mascotas", "kind": "VARIABLE", "icon": "dog"}
                    """))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson().extractingPath("$.id").isEqualTo(11);
    }

    @Test
    void create_returns409WhenTheNameIsTaken() {
        when(categoryService.create(any())).thenThrow(new CategoryNameTakenException("delivery"));

        assertThat(mvc.post().uri("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "delivery", "kind": "VARIABLE", "icon": "bike"}
                    """))
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson().extractingPath("$.title").isEqualTo("Category name already in use");
    }

    @Test
    void create_returns400WhenTheIconIsNotAllowed() {
        assertThat(mvc.post().uri("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "Cohetes", "kind": "VARIABLE", "icon": "rocket"}
                    """))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson().extractingPath("$.errors.icon").isNotNull();
    }

    @Test
    void delete_returns409WhenTheCategoryHasExpenses() {
        doThrow(new CategoryInUseException(3L)).when(categoryService).delete(3L);

        assertThat(mvc.delete().uri("/api/categories/3"))
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson().extractingPath("$.title").isEqualTo("Category in use");
    }

    @Test
    void archive_returnsTheArchivedCategory() {
        when(categoryService.archive(3L)).thenReturn(
                new CategoryResponse(3L, "Delivery", CategoryKind.VARIABLE, "bike", true));

        assertThat(mvc.post().uri("/api/categories/3/archive"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.archived").isEqualTo(true);
    }
}