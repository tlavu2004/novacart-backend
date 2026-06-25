package com.tlavu.novacart.modules.catalog.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateProductStockRequest(
        @NotNull(message = "Product stock quantity is required")
        @PositiveOrZero(message = "Product stock quantity must be greater than or equal to 0")
        Integer stockQuantity
) {}
