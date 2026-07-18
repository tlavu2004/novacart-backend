package com.tlavu.novacart.shared.application.exception.common;

import com.tlavu.novacart.shared.application.exception.base.BaseException;
import com.tlavu.novacart.shared.application.exception.code.contract.ErrorCode;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(ErrorCode errorCode) {

        super(errorCode, HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(ErrorCode errorCode, String message) {

        super(errorCode, HttpStatus.NOT_FOUND, message);
    }
}
