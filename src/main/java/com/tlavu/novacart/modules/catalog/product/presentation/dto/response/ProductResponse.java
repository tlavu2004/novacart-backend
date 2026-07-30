package com.tlavu.novacart.modules.catalog.product.presentation.dto.response;

import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.product.domain.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String slug,
        ProductStatus status,
        BigDecimal price,
        Integer stockQuantity,
        Long categoryId,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSlug(),
                product.getStatus(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory().getId(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getVersion()
        );
    }
}
