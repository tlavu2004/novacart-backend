package com.tlavu.novacart.modules.catalog.shared.presentation.mapper;

import com.tlavu.novacart.modules.catalog.shared.domain.repository.query.PageRequest;
import com.tlavu.novacart.modules.catalog.shared.domain.repository.query.SortDirection;
import com.tlavu.novacart.modules.catalog.shared.domain.repository.query.SortOrder;
import org.springframework.data.domain.Pageable;

public final class PageRequestMapper {

    private PageRequestMapper() {
    }

    public static PageRequest toDomain(Pageable pageable) {

        return new PageRequest(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().stream()
                        .map(order -> new SortOrder(
                                order.getProperty(),
                                SortDirection.valueOf(order.getDirection().name())
                        ))
                        .toList()
        );
    }
}
