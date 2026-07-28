package com.tlavu.novacart.modules.catalog.infrastructure.persistence.mapper.product;

import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.Instant;

record ProductRehydrationState(
        Long id,
        String name,
        String description,
        String slug,
        ProductStatus status,
        BigDecimal price,
        Integer stockQuantity,
        Category category,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {}
