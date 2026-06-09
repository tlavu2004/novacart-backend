package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CreateProductUseCase {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Product execute(
            String name,
            BigDecimal price,
            Integer stockQuantity,
            Long categoryId
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name invalid.");
        }
        if (price == null) {
            throw new IllegalArgumentException("Product price invalid.");
        }
        if (stockQuantity == null) {
            throw new IllegalArgumentException("Product stock quantity invalid.");
        }
        if (categoryId == null) {
            throw new IllegalArgumentException("Category id invalid.");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new IllegalStateException("Category not found."));

        if (productRepository.existsByName(name)) {
            throw new IllegalStateException("Product already exists.");
        }

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setCategory(category);

        return productRepository.save(product);
    }
}
