package com.tlavu.novacart.modules.catalog.shared.domain.repository.query;

public record SortOrder(
        String property,
        SortDirection direction
) {
}
