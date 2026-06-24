package com.tlavu.novacart.modules.catalog.presentation.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest (
        @Size(max = 255, message = "Category name must not exceed 255 characters")
        String name,

        @Size(max = 1000, message = "Category description must not exceed 1000 characters")
        String description,

        Boolean active
) {}
