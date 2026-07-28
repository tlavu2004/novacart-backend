package com.tlavu.novacart.modules.catalog.domain.repository.query;

import com.tlavu.novacart.modules.catalog.domain.enums.ProductStatus;

import java.math.BigDecimal;

public record ProductFilter(
        String name,
        ProductStatus status,
        Long categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {
}
