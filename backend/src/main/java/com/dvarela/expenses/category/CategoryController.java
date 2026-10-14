package com.dvarela.expenses.category;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.dvarela.expenses.household.CurrentHousehold;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CurrentHousehold currentHousehold;

    public CategoryController(CategoryService categoryService, CurrentHousehold currentHousehold) {
        this.categoryService = categoryService;
        this.currentHousehold = currentHousehold;
    }

    @GetMapping
    public List<CategoryResponse> findAll(
            @RequestParam(defaultValue = "false") boolean includeArchived) {
        return categoryService.findAll(currentHousehold.id(), includeArchived);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@Valid @RequestBody CategoryRequest request) {
        return categoryService.create(currentHousehold.id(), request);
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(currentHousehold.id(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        categoryService.delete(currentHousehold.id(), id);
    }

    @PostMapping("/{id}/archive")
    public CategoryResponse archive(@PathVariable Long id) {
        return categoryService.archive(currentHousehold.id(), id);
    }

    @PostMapping("/{id}/unarchive")
    public CategoryResponse unarchive(@PathVariable Long id) {
        return categoryService.unarchive(currentHousehold.id(), id);
    }
}