package com.tlavu.novacart.modules.catalog.product.application.usecase;

import com.tlavu.novacart.modules.catalog.category.application.exception.CategoryNotFoundException;
import com.tlavu.novacart.modules.catalog.product.application.service.ProductSlugAllocationService;
import com.tlavu.novacart.modules.catalog.category.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.category.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.shared.domain.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CreateProductUseCase {

    private final CategoryRepository categoryRepository;
    private final ProductSlugAllocationService productSlugAllocationService;

    public Product execute(
            String name,
            String description,
            BigDecimal price,
            Integer stockQuantity,
            Long categoryId
    ) {

        String normalizedName = name.trim();
        String slug = SlugUtils.generate(normalizedName);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        Product product = Product.create(normalizedName, description, slug, price, stockQuantity, category);

        return productSlugAllocationService.allocateAndPersist(product);
    }
}
