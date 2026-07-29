package com.tlavu.novacart.modules.catalog.product.domain.exception;

import com.tlavu.novacart.modules.catalog.product.domain.enums.ProductStatus;
import lombok.Getter;

@Getter
public class InvalidProductStatusTransitionException extends RuntimeException {

    private final ProductStatus from;
    private final ProductStatus to;

    public InvalidProductStatusTransitionException(ProductStatus from, ProductStatus to) {

        super("Cannot transition product status from %s to %s".formatted(from, to));
        this.from = from;
        this.to = to;
    }

}
