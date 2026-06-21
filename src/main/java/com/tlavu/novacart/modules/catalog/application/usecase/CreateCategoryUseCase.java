package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.application.exception.ConflictException;
import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
import com.tlavu.novacart.shared.exception.code.ErrorCode;
import com.tlavu.novacart.shared.util.SlugUtils;
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
        String slug = SlugUtils.generate(normalizedName);

        if (categoryRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new ConflictException(
                    ErrorCode.CATEGORY_ALREADY_EXISTS,
                    "Category '%s' already exists".formatted(normalizedName)
            );
        }

        if (categoryRepository.existsBySlug(slug)) {
            throw new ConflictException(
                    ErrorCode.CATEGORY_SLUG_ALREADY_EXISTS,
                    "Category with slug '%s' already exists".formatted(slug)
            );
        }

        Category category = new Category();
        category.setName(normalizedName);
        category.setDescription(description);
        category.setSlug(slug);
        category.setActive(true);

        return categoryRepository.save(category);
    }
}
