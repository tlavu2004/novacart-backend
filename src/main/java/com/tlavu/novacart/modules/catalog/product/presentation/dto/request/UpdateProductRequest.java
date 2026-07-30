package com.tlavu.novacart.modules.catalog.product.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateProductRequest(
        @NotNull(message = "Product version is required")
        @PositiveOrZero(message = "Product version must be greater than or equal to 0")
        Long version,

        @Size(max = 255, message = "Product name must not exceed 255 characters")
        String name,

        @Size(max = 1000, message = "Product description must not exceed 1000 characters")
        String description,

        @Positive(message = "Product price must be greater than 0")
        BigDecimal price,

        Long categoryId
) {}
