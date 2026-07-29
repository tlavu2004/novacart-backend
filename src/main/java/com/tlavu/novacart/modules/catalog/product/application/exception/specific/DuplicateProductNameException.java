package com.tlavu.novacart.modules.catalog.product.application.exception.specific;

import com.tlavu.novacart.modules.catalog.shared.application.exception.code.CatalogErrorCode;
import com.tlavu.novacart.shared.application.exception.common.ConflictException;

public class DuplicateProductNameException extends ConflictException {

    public DuplicateProductNameException(String name) {

        super(
                CatalogErrorCode.PRODUCT_ALREADY_EXISTS,
                "Product with name '%s' already exists".formatted(name)
        );
    }
}
