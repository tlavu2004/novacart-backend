package com.tlavu.novacart.shared.dto;

public record FieldErrorResponse(
        String field,
        String message
) {}
