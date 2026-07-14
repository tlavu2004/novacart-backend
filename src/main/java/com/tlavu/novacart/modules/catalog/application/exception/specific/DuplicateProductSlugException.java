package com.tlavu.novacart.modules.catalog.application.exception.specific;

import com.tlavu.novacart.modules.catalog.application.exception.code.CatalogErrorCode;
import com.tlavu.novacart.shared.application.exception.common.ConflictException;

public class DuplicateProductSlugException extends ConflictException {

    public DuplicateProductSlugException(String slug) {
        super(
                CatalogErrorCode.PRODUCT_SLUG_ALREADY_EXISTS,
                "Product with slug '%s' already exists".formatted(slug)
        );
    }
}
