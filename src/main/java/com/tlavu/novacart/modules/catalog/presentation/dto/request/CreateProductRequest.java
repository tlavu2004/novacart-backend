package com.tlavu.novacart.modules.catalog.presentation.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductRequest (
        @NotBlank
        @Size(max = 255)
        String name,

        @Size(max = 1000)
        String description,

        @NotNull
        @Positive
        BigDecimal price,

        @NotNull
        @PositiveOrZero
        Integer stockQuantity,

        @NotNull
        Long categoryId
) {}
