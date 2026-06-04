package com.tlavu.novacart.system.health.controller;

import com.tlavu.novacart.system.health.dto.HealthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/health/db")
    public ResponseEntity<HealthResponse> checkDatabaseHealth() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);

            return ResponseEntity.ok(
                    new HealthResponse(
                            "UP",
                            "Database connection is healthy",
                            null
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new HealthResponse(
                            "DOWN",
                            "Database connection failed",
                            e.getMessage()
                    )
            );
        }
    }
}
