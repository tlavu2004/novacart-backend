package com.tlavu.novacart.system.health.dto;

public record HealthResponse(
        String status,
        String message,
        String error
) {}
