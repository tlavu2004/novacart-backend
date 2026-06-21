package com.tlavu.novacart.modules.catalog.presentation.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductRequest (
        @NotBlank(message = "Product name is required")
        @Size(max = 255, message = "Product name must not exceed 255 characters")
        String name,

        @Size(max = 1000, message = "Product description must not exceed 1000 characters")
        String description,

        @NotNull(message = "Product price is required")
        @Positive(message = "Product price must be greater than 0")
        BigDecimal price,

        @NotNull(message = "Product stock quantity is required")
        @PositiveOrZero(message = "Product stock quantity must be greater than or equal to 0")
        Integer stockQuantity,

        @NotNull(message = "Category is required")
        Long categoryId
) {}
