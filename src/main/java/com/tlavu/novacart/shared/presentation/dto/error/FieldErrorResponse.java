package com.tlavu.novacart.shared.presentation.dto.error;

public record FieldErrorResponse(
        String field,
        String message
) {}
