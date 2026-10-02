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
    public List<CategoryResponse> findAll(boolean includeArchived) {
        List<Category> categories = includeArchived
                ? categoryRepository.findAllByOrderByNameAsc()
                : categoryRepository.findAllByArchivedFalseOrderByNameAsc();
        return categories.stream().map(CategoryResponse::from).toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String name = request.name().trim();
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new CategoryNameTakenException(name);
        }
        Category saved = categoryRepository.save(new Category(name, request.kind(), request.icon()));
        return CategoryResponse.from(saved);
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findCategory(id);
        String name = request.name().trim();
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new CategoryNameTakenException(name);
        }
        category.update(name, request.kind(), request.icon());
        return CategoryResponse.from(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = findCategory(id);
        if (categoryRepository.isInUse(id)) {
            throw new CategoryInUseException(id);
        }
        categoryRepository.delete(category);
    }

    @Transactional
    public CategoryResponse archive(Long id) {
        Category category = findCategory(id);
        category.archive();
        return CategoryResponse.from(category);
    }

    @Transactional
    public CategoryResponse unarchive(Long id) {
        Category category = findCategory(id);
        category.unarchive();
        return CategoryResponse.from(category);
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }
}