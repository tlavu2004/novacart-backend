package com.tlavu.novacart.modules.catalog.shared.domain.query;

public record SortOrder(
        String property,
        SortDirection direction
) {
}
