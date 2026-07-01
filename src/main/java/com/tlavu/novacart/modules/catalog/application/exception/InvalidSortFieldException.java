package com.tlavu.novacart.modules.catalog.application.exception;

import com.tlavu.novacart.shared.exception.base.BaseException;
import com.tlavu.novacart.shared.exception.code.ErrorCode;

public class InvalidSortFieldException extends BaseException {

    public InvalidSortFieldException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidSortFieldException(ErrorCode errorCode, String message) {

        super(errorCode, message);
    }
}
