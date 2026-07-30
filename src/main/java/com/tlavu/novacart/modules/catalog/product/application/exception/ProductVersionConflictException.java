package com.tlavu.novacart.modules.catalog.product.application.exception;

import com.tlavu.novacart.modules.catalog.shared.application.exception.code.catalog.CatalogErrorCode;
import com.tlavu.novacart.shared.application.exception.common.ConflictException;

public class ProductVersionConflictException extends ConflictException {

    public ProductVersionConflictException(Long id) {
        super(
                CatalogErrorCode.PRODUCT_VERSION_CONFLICT,
                "Product with id=%d was modified by another request".formatted(id)
        );
    }
}
