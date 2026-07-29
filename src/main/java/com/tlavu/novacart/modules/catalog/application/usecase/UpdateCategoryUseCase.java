package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.application.exception.specific.CategoryNotFoundException;
import com.tlavu.novacart.modules.catalog.application.exception.specific.DuplicateCategoryNameException;
import com.tlavu.novacart.modules.catalog.application.exception.specific.DuplicateCategorySlugException;
import com.tlavu.novacart.shared.application.exception.code.global.GlobalErrorCode;
import com.tlavu.novacart.shared.application.exception.common.InvalidInputException;
import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
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
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (name != null) {

            String normalizedName = name.trim().replaceAll("\\s+", " ");

            if (normalizedName.isEmpty()) {

                throw new InvalidInputException(
                        GlobalErrorCode.INVALID_INPUT,
                        "Category name must not be blank"
                );
            }

            if (categoryRepository.existsByNameIgnoreCaseAndIdNot(normalizedName, id)) {

                throw new DuplicateCategoryNameException(normalizedName);
            }

            String slug = SlugUtils.generate(normalizedName);

            if (categoryRepository.existsBySlugAndIdNot(slug, id)) {

                throw new DuplicateCategorySlugException(slug);
            }

            category.rename(normalizedName, slug);
        }

        if (description != null) {

            category.changeDescription(description);
        }

        if (active != null) {

            category.changeActive(active);
        }

        return categoryRepository.save(category);
    }
}
