package com.tlavu.novacart.shared.application.exception.common;

import com.tlavu.novacart.shared.application.exception.base.BaseException;
import com.tlavu.novacart.shared.application.exception.code.contract.ErrorCode;

public class ConflictException extends BaseException {

    public ConflictException(ErrorCode errorCode) {

        super(errorCode);
    }

    public ConflictException(ErrorCode errorCode, String message) {

        super(errorCode, message);
    }
}
