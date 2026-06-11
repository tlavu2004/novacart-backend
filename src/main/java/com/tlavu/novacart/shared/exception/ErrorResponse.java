package com.tlavu.novacart.shared.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        int status,
        String message,
        Instant timestamp,
        String path,
        Map<String, Object> errors
) {}
