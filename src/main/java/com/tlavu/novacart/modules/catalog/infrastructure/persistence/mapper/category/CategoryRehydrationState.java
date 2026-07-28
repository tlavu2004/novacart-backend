package com.tlavu.novacart.modules.catalog.infrastructure.persistence.mapper.category;

import java.time.Instant;

record CategoryRehydrationState(
        Long id,
        String name,
        String description,
        String slug,
        boolean active,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {}
