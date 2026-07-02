package com.tlavu.novacart.shared.application.exception.common;

import com.tlavu.novacart.shared.application.exception.base.BaseException;
import com.tlavu.novacart.shared.application.exception.code.contract.ErrorCode;

public class InvalidSortFieldException extends BaseException {

    public InvalidSortFieldException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidSortFieldException(ErrorCode errorCode, String message) {

        super(errorCode, message);
    }
}
