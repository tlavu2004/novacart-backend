package com.tlavu.novacart.shared.application.exception.common;

import com.tlavu.novacart.shared.application.exception.base.BaseException;
import com.tlavu.novacart.shared.application.exception.code.contract.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidInputException extends BaseException {

    public InvalidInputException(ErrorCode errorCode) {

        super(errorCode, HttpStatus.BAD_REQUEST);
    }

    public InvalidInputException(ErrorCode errorCode, String message) {

        super(errorCode, HttpStatus.BAD_REQUEST, message);
    }
}
