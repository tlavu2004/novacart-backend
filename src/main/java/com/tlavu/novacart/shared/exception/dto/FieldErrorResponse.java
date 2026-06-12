package com.tlavu.novacart.shared.exception.dto;

public record FieldErrorResponse(
        String field,
        String message
) {}
