package com.tlavu.novacart.modules.catalog.application.exception;

import com.tlavu.novacart.shared.exception.base.BaseException;
import com.tlavu.novacart.shared.exception.code.ErrorCode;

public class ValidationException extends BaseException {

    public ValidationException(ErrorCode errorCode) {
        super(errorCode, errorCode.getDefaultMessage());
    }

    public ValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
