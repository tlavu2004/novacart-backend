package com.tlavu.novacart.shared.application.exception.common;

import com.tlavu.novacart.shared.application.exception.base.BaseException;
import com.tlavu.novacart.shared.application.exception.code.contract.ErrorCode;
import org.springframework.http.HttpStatus;

public class ConflictException extends BaseException {

    public ConflictException(ErrorCode errorCode) {

        super(errorCode, HttpStatus.CONFLICT);
    }

    public ConflictException(ErrorCode errorCode, String message) {

        super(errorCode, HttpStatus.CONFLICT, message);
    }
}
