package com.tlavu.novacart.modules.catalog.product.application.exception.specific;

import com.tlavu.novacart.modules.catalog.shared.application.exception.code.CatalogErrorCode;
import com.tlavu.novacart.shared.application.exception.common.ResourceNotFoundException;

public class ProductNotFoundException extends ResourceNotFoundException {

    public ProductNotFoundException(Long id) {

        super(
                CatalogErrorCode.PRODUCT_NOT_FOUND,
                "Product with id=%d not found".formatted(id));
    }
}
