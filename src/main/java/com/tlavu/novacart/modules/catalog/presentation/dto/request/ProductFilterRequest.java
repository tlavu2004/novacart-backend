package com.tlavu.novacart.modules.catalog.presentation.dto.request;

import com.tlavu.novacart.modules.catalog.domain.enums.ProductStatus;

import java.math.BigDecimal;

public record ProductFilterRequest(
        String name,
        ProductStatus status,
        Long categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {}