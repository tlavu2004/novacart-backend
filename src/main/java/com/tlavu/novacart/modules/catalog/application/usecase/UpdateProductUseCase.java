package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.shared.application.exception.code.global.GlobalErrorCode;
import com.tlavu.novacart.shared.application.exception.common.ConflictException;
import com.tlavu.novacart.shared.application.exception.common.InvalidInputException;
import com.tlavu.novacart.shared.application.exception.common.ResourceNotFoundException;

import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.domain.repository.ProductRepository;
import com.tlavu.novacart.modules.catalog.application.exception.code.CatalogErrorCode;
import com.tlavu.novacart.modules.catalog.infrastructure.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateProductUseCase {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public Product execute(
            Long id,
            String name,
            String description,
            BigDecimal price,
            Long categoryId
    ) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CatalogErrorCode.PRODUCT_NOT_FOUND,
                        "Product with id=%d not found".formatted(id)
                ));

        if (name != null) {

            String normalizedName = name.trim();

            if (normalizedName.isEmpty()) {
                throw new InvalidInputException(
                        GlobalErrorCode.INVALID_INPUT,
                        "Product name must not be blank"
                );
            }

            if (productRepository.existsByNameIgnoreCaseAndIdNot(normalizedName, id)) {
                throw new ConflictException(
                        CatalogErrorCode.PRODUCT_ALREADY_EXISTS,
                        "Product '%s' already exists".formatted(normalizedName)
                );
            }

            String slug = SlugUtils.generate(normalizedName);

            if (productRepository.existsBySlugAndIdNot(slug, id)) {
                throw new ConflictException(
                        CatalogErrorCode.PRODUCT_SLUG_ALREADY_EXISTS,
                        "Product with slug '%s' already exists".formatted(slug)
                );
            }

            product.setName(normalizedName);
            product.setSlug(slug);
        }

        if (description != null) {

            product.setDescription(description);
        }

        if (price != null) {

            product.setPrice(price);
        }

        if (categoryId != null) {

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            CatalogErrorCode.CATEGORY_NOT_FOUND,
                            "Category with id=%d not found".formatted(categoryId)
                    ));

            product.setCategory(category);
        }

        return productRepository.save(product);
    }
}
