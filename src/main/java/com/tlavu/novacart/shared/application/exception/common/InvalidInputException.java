package com.tlavu.novacart.shared.application.exception.common;

import com.tlavu.novacart.shared.application.exception.base.BaseException;
import com.tlavu.novacart.shared.application.exception.code.ErrorCode;

public class InvalidInputException extends BaseException {

    public InvalidInputException(ErrorCode errorCode) {

        super(errorCode, errorCode.getDefaultMessage());
    }

    public InvalidInputException(ErrorCode errorCode, String message) {

        super(errorCode, message);
    }
}
