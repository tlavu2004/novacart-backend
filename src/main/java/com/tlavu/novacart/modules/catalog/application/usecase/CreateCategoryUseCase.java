package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.application.exception.ConflictException;
import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
import com.tlavu.novacart.shared.exception.code.ErrorCode;
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

        String normalizedName = name.trim();

        if (categoryRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new ConflictException(
                    ErrorCode.CATEGORY_ALREADY_EXISTS,
                    "Category '%s' already exists".formatted(normalizedName)
            );
        }

        Category category = new Category();
        category.setName(normalizedName);
        category.setDescription(description);

        return categoryRepository.save(category);
    }
}
