package com.tlavu.novacart.modules.catalog.category.application.exception;

import com.tlavu.novacart.modules.catalog.shared.domain.exception.CatalogErrorCode;
import com.tlavu.novacart.shared.application.exception.common.ConflictException;

public class CategoryHasActiveProductsException extends ConflictException {

    public CategoryHasActiveProductsException(Long categoryId) {

        super(
                CatalogErrorCode.CATEGORY_HAS_ACTIVE_PRODUCTS,
                "Category with id=%d has active products and cannot be deleted".formatted(categoryId)
        );
    }
}
