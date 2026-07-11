package com.tlavu.novacart.shared.application.exception.common;

import com.tlavu.novacart.shared.application.exception.base.BaseException;
import com.tlavu.novacart.shared.application.exception.code.contract.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidSortFieldException extends BaseException {

    public InvalidSortFieldException(ErrorCode errorCode) {

        super(errorCode, HttpStatus.BAD_REQUEST);
    }

    public InvalidSortFieldException(ErrorCode errorCode, String message) {

        super(errorCode, HttpStatus.BAD_REQUEST, message);
    }
}
