package com.tlavu.novacart.shared.exception.dto;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        int status,
        String code,
        String message,
        Instant timestamp,
        String path,
        List<FieldErrorResponse> errors
) {}
