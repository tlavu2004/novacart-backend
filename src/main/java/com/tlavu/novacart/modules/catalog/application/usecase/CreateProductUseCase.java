package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.application.exception.ConflictException;
import com.tlavu.novacart.modules.catalog.application.exception.ResourceNotFoundException;
import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.domain.enums.ProductStatus;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.domain.repository.ProductRepository;
import com.tlavu.novacart.shared.application.exception.code.ErrorCode;
import com.tlavu.novacart.modules.catalog.infrastructure.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateProductUseCase {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Product execute(
            String name,
            String description,
            BigDecimal price,
            Integer stockQuantity,
            Long categoryId
    ) {

        String normalizedName = name.trim();
        String slug = SlugUtils.generate(normalizedName);

        if (productRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new ConflictException(
                    ErrorCode.PRODUCT_ALREADY_EXISTS,
                    "Product '%s' already exists".formatted(normalizedName)
            );
        }

        if (productRepository.existsBySlug(slug)) {
            throw new ConflictException(
                    ErrorCode.PRODUCT_SLUG_ALREADY_EXISTS,
                    "Product with slug '%s' already exists".formatted(slug)
            );
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CATEGORY_NOT_FOUND,
                        "Category with id=%d not found".formatted(categoryId)
                ));

        Product product = new Product();
        product.setName(normalizedName);
        product.setDescription(description);
        product.setPrice(price);
        product.setSlug(slug);
        product.setStatus(ProductStatus.DRAFT);
        product.setStockQuantity(stockQuantity);
        product.setCategory(category);

        return productRepository.save(product);
    }
}
