package com.dvarela.expenses.category;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll(Long householdId, boolean includeArchived) {
        List<Category> categories = includeArchived
                ? categoryRepository.findAllByHouseholdIdOrderByNameAsc(householdId)
                : categoryRepository.findAllByHouseholdIdAndArchivedFalseOrderByNameAsc(householdId);
        return categories.stream().map(CategoryResponse::from).toList();
    }

    @Transactional
    public CategoryResponse create(Long householdId, CategoryRequest request) {
        String name = request.name().trim();
        if (categoryRepository.existsByHouseholdIdAndNameIgnoreCase(householdId, name)) {
            throw new CategoryNameTakenException(name);
        }
        Category saved = categoryRepository.save(
                new Category(householdId, name, request.kind(), request.icon()));
        return CategoryResponse.from(saved);
    }

    @Transactional
    public CategoryResponse update(Long householdId, Long id, CategoryRequest request) {
        Category category = findCategory(householdId, id);
        String name = request.name().trim();
        if (categoryRepository.existsByHouseholdIdAndNameIgnoreCaseAndIdNot(householdId, name, id)) {
            throw new CategoryNameTakenException(name);
        }
        category.update(name, request.kind(), request.icon());
        return CategoryResponse.from(category);
    }

    @Transactional
    public void delete(Long householdId, Long id) {
        Category category = findCategory(householdId, id);
        if (categoryRepository.isInUse(id)) {
            throw new CategoryInUseException(id);
        }
        categoryRepository.delete(category);
    }

    @Transactional
    public CategoryResponse archive(Long householdId, Long id) {
        Category category = findCategory(householdId, id);
        category.archive();
        return CategoryResponse.from(category);
    }

    @Transactional
    public CategoryResponse unarchive(Long householdId, Long id) {
        Category category = findCategory(householdId, id);
        category.unarchive();
        return CategoryResponse.from(category);
    }

    private Category findCategory(Long householdId, Long id) {
        return categoryRepository.findByIdAndHouseholdId(id, householdId)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }
}