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

    INTERNAL_SERVER_ERROR("SYS_500", "Internal server error");

    private final String code;
    private final String defaultMessage;
}
