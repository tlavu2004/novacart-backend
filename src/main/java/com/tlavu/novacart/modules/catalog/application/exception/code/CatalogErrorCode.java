package com.tlavu.novacart.modules.catalog.application.exception.code;

import com.tlavu.novacart.shared.application.exception.code.contract.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CatalogErrorCode implements ErrorCode {

    CATEGORY_ALREADY_EXISTS("CAT_001", "Category already exists"),
    CATEGORY_SLUG_ALREADY_EXISTS("CAT_002", "Category slug already exists"),
    CATEGORY_NOT_FOUND("CAT_003", "Category not found"),
    CATEGORY_HAS_ACTIVE_PRODUCTS("CAT_004", "Category has active products"),

    PRODUCT_ALREADY_EXISTS("PROD_001", "Product already exists"),
    PRODUCT_SLUG_ALREADY_EXISTS("PROD_002", "Product slug already exists"),
    PRODUCT_NOT_FOUND("PROD_003", "Product not found"),
    INVALID_STATUS_TRANSITION("PROD_004", "Invalid status transition");

    private final String code;
    private final String defaultMessage;
}
