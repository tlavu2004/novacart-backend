package com.tlavu.novacart.modules.catalog.category.application.usecase;

import com.tlavu.novacart.modules.catalog.category.application.exception.DuplicateCategoryNameException;
import com.tlavu.novacart.modules.catalog.category.application.exception.DuplicateCategorySlugException;
import com.tlavu.novacart.modules.catalog.category.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.category.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.shared.domain.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public Category execute(
            String name,
            String description
    ) {

        String normalizedName = name.trim().replaceAll("\\s+", " ");
        String slug = SlugUtils.generate(normalizedName);

        if (categoryRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new DuplicateCategoryNameException(normalizedName);
        }

        if (categoryRepository.existsBySlug(slug)) {
            throw new DuplicateCategorySlugException(slug);
        }

        Category category = Category.create(normalizedName, description, slug);

        return categoryRepository.save(category);
    }
}
