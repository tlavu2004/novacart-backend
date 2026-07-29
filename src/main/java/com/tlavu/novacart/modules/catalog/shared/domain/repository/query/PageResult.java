package com.tlavu.novacart.modules.catalog.shared.domain.repository.query;

import java.util.List;

public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
