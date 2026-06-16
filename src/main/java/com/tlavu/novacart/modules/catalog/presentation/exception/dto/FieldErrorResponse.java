package com.tlavu.novacart.modules.catalog.presentation.exception.dto;

public record FieldErrorResponse(
        String field,
        String message
) {}
