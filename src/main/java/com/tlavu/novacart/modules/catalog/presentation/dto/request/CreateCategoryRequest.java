package com.tlavu.novacart.modules.catalog.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest (
        @NotBlank(message = "Category name is required")
        @Size(max = 255, message = "Category name must not exceed 255 characters")
        String name,

        @Size(max = 1000)
        String description
) {}
