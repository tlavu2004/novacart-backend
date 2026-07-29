package com.tlavu.novacart.modules.catalog.category.application.usecase;

import com.tlavu.novacart.modules.catalog.category.application.exception.specific.CategoryHasActiveProductsException;
import com.tlavu.novacart.modules.catalog.category.application.exception.specific.CategoryNotFoundException;
import com.tlavu.novacart.modules.catalog.category.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.category.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.product.domain.repository.ProductRepository;
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
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (productRepository.existsByCategoryId(id)) {
            throw new CategoryHasActiveProductsException(id);
        }

        Instant now = Instant.now();

        category.softDelete(now);

        categoryRepository.save(category);
    }
}
