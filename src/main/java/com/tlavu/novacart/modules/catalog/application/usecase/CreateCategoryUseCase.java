package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public Category execute(
            String name,
            String description
    ) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name invalid.");
        }

        if (categoryRepository.existsByName(name)) {
            throw new IllegalStateException("Category already exists.");
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);

        return categoryRepository.save(category);
    }
}
