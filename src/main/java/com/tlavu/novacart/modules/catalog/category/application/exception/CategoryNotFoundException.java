package com.tlavu.novacart.modules.catalog.category.application.exception;

import com.tlavu.novacart.modules.catalog.shared.application.exception.CatalogErrorCode;
import com.tlavu.novacart.shared.application.exception.common.ResourceNotFoundException;

public class CategoryNotFoundException extends ResourceNotFoundException {

    public CategoryNotFoundException(Long id) {

        super(
                CatalogErrorCode.CATEGORY_NOT_FOUND,
                "Category with id=%d not found".formatted(id)
        );
    }
}
