package com.tlavu.novacart.shared.dto.error;

public record FieldErrorResponse(
        String field,
        String message
) {}
