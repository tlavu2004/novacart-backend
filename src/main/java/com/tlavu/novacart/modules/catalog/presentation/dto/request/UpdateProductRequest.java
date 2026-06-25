package com.tlavu.novacart.modules.catalog.presentation.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateProductRequest(
        @Size(max = 255, message = "Product name must not exceed 255 characters")
        String name,

        @Size(max = 1000, message = "Product description must not exceed 1000 characters")
        String description,

        @Positive(message = "Product price must be greater than 0")
        BigDecimal price,

        Long categoryId
) {}
