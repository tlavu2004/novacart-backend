package com.tlavu.novacart.modules.catalog.application.exception;

import com.tlavu.novacart.shared.application.exception.base.BaseException;
import com.tlavu.novacart.shared.application.exception.code.ErrorCode;

public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(ErrorCode errorCode) {

        super(errorCode);
    }

    public ResourceNotFoundException(ErrorCode errorCode, String message) {

        super(errorCode, message);
    }
}
