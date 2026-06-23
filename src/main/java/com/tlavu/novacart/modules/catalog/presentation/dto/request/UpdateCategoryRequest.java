package com.tlavu.novacart.modules.catalog.presentation.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest (
        @Size(max = 255, message = "Category name must not exceed 255 characters")
        String name,

        String description,

        Boolean active
) {}
