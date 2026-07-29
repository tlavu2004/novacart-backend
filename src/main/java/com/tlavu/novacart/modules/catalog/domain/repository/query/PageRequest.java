package com.tlavu.novacart.modules.catalog.domain.repository.query;

import java.util.List;

public record PageRequest(
        int page,
        int size,
        List<SortOrder> sortOrders
) {
}
