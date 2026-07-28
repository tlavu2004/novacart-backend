package com.tlavu.novacart.modules.catalog.domain.repository.query;

public record SortOrder(
        String property,
        SortDirection direction
) {
}
