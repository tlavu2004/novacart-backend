package com.tlavu.novacart.shared.dto.error;

import com.tlavu.novacart.shared.dto.response.FieldErrorResponse;

import java.util.List;

public record ApiError(
        int status,
        String code,
        String message,
        String path,
        List<FieldErrorResponse> fieldErrors
) {}
