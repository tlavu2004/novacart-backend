package com.tlavu.novacart.shared.dto;

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
