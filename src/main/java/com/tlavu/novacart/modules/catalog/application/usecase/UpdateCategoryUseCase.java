package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.application.exception.ConflictException;
import com.tlavu.novacart.modules.catalog.application.exception.InvalidInputException;
import com.tlavu.novacart.modules.catalog.application.exception.ResourceNotFoundException;
import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
import com.tlavu.novacart.shared.exception.code.ErrorCode;
import com.tlavu.novacart.modules.catalog.infrastructure.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public Category execute(
            Long id,
            String name,
            String description,
            Boolean active
    ) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CATEGORY_NOT_FOUND,
                        "Category with id=%d not found".formatted(id)
                ));

        if (name != null) {

            String normalizedName = name.trim();

            if (normalizedName.isEmpty()) {

                throw new InvalidInputException(
                        ErrorCode.INVALID_INPUT,
                        "Category name must not be blank"
                );
            }

            if (categoryRepository.existsByNameIgnoreCaseAndIdNot(normalizedName, id)) {

                throw new ConflictException(
                        ErrorCode.CATEGORY_ALREADY_EXISTS,
                        "Category '%s' already exists".formatted(normalizedName)
                );
            }

            String slug = SlugUtils.generate(normalizedName);

            if (categoryRepository.existsBySlugAndIdNot(slug, id)) {

                throw new ConflictException(
                        ErrorCode.CATEGORY_SLUG_ALREADY_EXISTS,
                        "Category with slug '%s' already exists".formatted(slug)
                );
            }

            category.setName(normalizedName);
            category.setSlug(slug);
        }

        if (description != null) {

            category.setDescription(description);
        }

        if (active != null) {

            category.setActive(active);
        }

        return categoryRepository.save(category);
    }
}
