package com.tlavu.novacart.modules.catalog.presentation.dto.response;

import com.tlavu.novacart.modules.catalog.domain.entity.Category;

import java.time.Instant;

public record CategoryResponse (
        Long id,
        String name,
        String slug,
        boolean active,
        String description,
        Instant createdAt,
        Instant updatedAt
) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.isActive(),
                category.getDescription(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
