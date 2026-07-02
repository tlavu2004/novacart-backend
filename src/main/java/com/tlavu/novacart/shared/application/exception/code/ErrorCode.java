package com.tlavu.novacart.shared.application.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    CATEGORY_ALREADY_EXISTS("CAT_001", "Category already exists"),
    CATEGORY_SLUG_ALREADY_EXISTS("CAT_002", "Category slug already exists"),
    CATEGORY_NOT_FOUND("CAT_003", "Category not found"),
    CATEGORY_HAS_ACTIVE_PRODUCTS("CAT_004", "Category has active products"),

    PRODUCT_ALREADY_EXISTS("PROD_001", "Product already exists"),
    PRODUCT_SLUG_ALREADY_EXISTS("PROD_002", "Product slug already exists"),
    PRODUCT_NOT_FOUND("PROD_003", "Product not found"),
    INVALID_STATUS_TRANSITION("PROD_004", "Invalid status transition"),

    VALIDATION_FAILED("VAL_001", "Validation failed"),
    INVALID_INPUT("VAL_002", "Invalid input"),
    INVALID_SORT_FIELD("VAL_003", "Invalid sort field"),

    INTERNAL_SERVER_ERROR("SYS_500", "Internal server error");

    private final String code;
    private final String defaultMessage;
}
