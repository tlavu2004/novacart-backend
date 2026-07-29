package com.tlavu.novacart.modules.catalog.product.domain.exception;

import com.tlavu.novacart.modules.catalog.shared.application.exception.CatalogErrorCode;
import com.tlavu.novacart.modules.catalog.product.domain.enums.ProductStatus;
import com.tlavu.novacart.shared.application.exception.common.InvalidInputException;

public class InvalidProductStatusTransitionException extends InvalidInputException {

    public InvalidProductStatusTransitionException(ProductStatus from, ProductStatus to) {

        super(
                CatalogErrorCode.INVALID_PRODUCT_STATUS_TRANSITION,
                "Cannot transition product status from %s to %s".formatted(from, to)
        );
    }
}
