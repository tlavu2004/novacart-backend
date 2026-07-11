package com.tlavu.novacart.shared.application.exception.code.global;

import com.tlavu.novacart.shared.application.exception.code.contract.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements ErrorCode {

    VALIDATION_FAILED("VAL_001", "Validation failed"),
    INVALID_INPUT("VAL_002", "Invalid input"),
    INVALID_SORT_FIELD("VAL_003", "Invalid sort field"),
    INVALID_PARAMETER_TYPE("VAL_004", "Invalid parameter type"),

    RESOURCE_NOT_FOUND("SYS_001", "Resource not found"),
    METHOD_NOT_ALLOWED("SYS_002", "HTTP method not allowed"),
    INTERNAL_SERVER_ERROR("SYS_003", "Internal server error");

    private final String code;
    private final String defaultMessage;
}
