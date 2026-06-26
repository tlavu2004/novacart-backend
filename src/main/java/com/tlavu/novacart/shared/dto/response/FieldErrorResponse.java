package com.tlavu.novacart.shared.dto.response;

public record FieldErrorResponse(
        String field,
        String message
) {}
