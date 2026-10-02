package com.dvarela.expenses.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
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
}