package com.tlavu.novacart.modules.catalog.product.application.usecase;

import com.tlavu.novacart.modules.catalog.category.application.exception.CategoryNotFoundException;
import com.tlavu.novacart.modules.catalog.category.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.category.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.product.application.exception.ProductNotFoundException;
import com.tlavu.novacart.modules.catalog.product.application.exception.ProductVersionConflictException;
import com.tlavu.novacart.modules.catalog.product.application.service.ProductSlugAllocationService;
import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.product.domain.repository.ProductRepository;
import com.tlavu.novacart.shared.application.exception.code.global.GlobalErrorCode;
import com.tlavu.novacart.shared.application.exception.common.InvalidInputException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UpdateProductUseCase {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductSlugAllocationService productSlugAllocationService;

    public Product execute(
            Long id,
            Long expectedVersion,
            String name,
            String description,
            BigDecimal price,
            Long categoryId
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (!Objects.equals(product.getVersion(), expectedVersion)) {
            throw new ProductVersionConflictException(id);
        }

        boolean nameChanged = false;

        if (name != null) {
            String normalizedName = name.trim();

            if (normalizedName.isEmpty()) {
                throw new InvalidInputException(
                        GlobalErrorCode.INVALID_INPUT,
                        "Product name must not be blank"
                );
            }

            nameChanged = !normalizedName.equals(product.getName());
            if (nameChanged) {
                product.rename(normalizedName, product.getSlug());
            }
        }

        if (description != null) {
            product.changeDescription(description);
        }

        if (price != null) {
            product.changePrice(price);
        }

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException(categoryId));

            product.moveToCategory(category);
        }

        try {
            if (nameChanged) {
                return productSlugAllocationService.allocateAndPersist(product);
            }

            return productRepository.save(product);
        } catch (ObjectOptimisticLockingFailureException exception) {
            throw new ProductVersionConflictException(id);
        }
    }
}
