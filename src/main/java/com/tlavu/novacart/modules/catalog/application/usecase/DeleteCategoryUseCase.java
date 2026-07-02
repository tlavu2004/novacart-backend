package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.shared.application.exception.common.ConflictException;
import com.tlavu.novacart.shared.application.exception.common.ResourceNotFoundException;
import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.domain.repository.ProductRepository;
import com.tlavu.novacart.shared.application.exception.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public void execute(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CATEGORY_NOT_FOUND,
                        "Category with id=%d not found".formatted(id)
                ));

        if (productRepository.existsByCategoryId(id)) {
            throw new ConflictException(
                    ErrorCode.CATEGORY_HAS_ACTIVE_PRODUCTS,
                    "Category with id=%d still has active products".formatted(id)
            );
        }

        Instant now = Instant.now();

        category.setActive(false);
        category.setDeletedAt(now);

        categoryRepository.save(category);
    }
}
