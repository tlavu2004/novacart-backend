package com.tlavu.novacart.modules.catalog.application.exception;

import com.tlavu.novacart.shared.exception.base.BaseException;
import com.tlavu.novacart.shared.exception.code.ErrorCode;

public class ConflictException extends BaseException {

    public ConflictException(ErrorCode errorCode) {

        super(errorCode);
    }

    public ConflictException(ErrorCode errorCode, String message) {

        super(errorCode, message);
    }
}
