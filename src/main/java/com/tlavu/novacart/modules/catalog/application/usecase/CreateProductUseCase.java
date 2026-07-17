package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.application.exception.specific.CategoryNotFoundException;
import com.tlavu.novacart.modules.catalog.application.exception.specific.DuplicateProductNameException;
import com.tlavu.novacart.modules.catalog.application.exception.specific.DuplicateProductSlugException;
import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.domain.repository.ProductRepository;
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
            throw new DuplicateProductNameException(normalizedName);
        }

        if (productRepository.existsBySlug(slug)) {
            throw new DuplicateProductSlugException(slug);
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        Product product = new Product();
        product.setName(normalizedName);
        product.setDescription(description);
        product.setPrice(price);
        product.setSlug(slug);
        product.setStockQuantity(stockQuantity);
        product.setCategory(category);

        return productRepository.save(product);
    }
}
