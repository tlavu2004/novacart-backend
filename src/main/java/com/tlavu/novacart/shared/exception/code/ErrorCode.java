package com.tlavu.novacart.shared.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    CATEGORY_ALREADY_EXISTS("CAT_001", "Category already exists"),
    CATEGORY_SLUG_ALREADY_EXISTS("CAT_002", "Category slug already exists"),
    CATEGORY_NOT_FOUND("CAT_003", "Category not found"),

    PRODUCT_ALREADY_EXISTS("PROD_001", "Product already exists"),
    PRODUCT_NOT_FOUND("PROD_002", "Product not found"),
    PRODUCT_SLUG_ALREADY_EXISTS("PROD_003", "Product slug already exists"),

    VALIDATION_FAILED("VAL_001", "Validation failed"),

    INTERNAL_SERVER_ERROR("SYS_500", "Internal server error");

    private final String code;
    private final String defaultMessage;
}
