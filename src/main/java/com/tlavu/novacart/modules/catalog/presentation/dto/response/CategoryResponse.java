package com.tlavu.novacart.modules.catalog.presentation.dto.response;

import com.tlavu.novacart.modules.catalog.domain.entity.Category;

import java.time.Instant;

public record CategoryResponse (
        Long id,
        String name,
        String description,
        Instant createdAt,
        Instant updateAt
) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
