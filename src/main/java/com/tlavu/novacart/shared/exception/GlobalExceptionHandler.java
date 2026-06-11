package com.tlavu.novacart.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {

        log.warn("Request failed at {}: {}", request.getRequestURI(), ex.getMessage());

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                "Invalid request: " + ex.getMessage(),
                Instant.now(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(status)
                .body(errorResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex,
            HttpServletRequest request
    ) {

        log.warn("Request failed at {}: {}", request.getRequestURI(), ex.getMessage());

        HttpStatus status = HttpStatus.CONFLICT;

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                "Conflict: " + ex.getMessage(),
                Instant.now(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(status)
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        log.warn("Request failed at {}: {}", request.getRequestURI(), ex.getMessage());

        HttpStatus status = HttpStatus.BAD_REQUEST;

        Map<String, Object> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), "Invalid value"),
                        (msg1, msg2) -> msg1
                ));

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                "Validation failed",
                Instant.now(),
                request.getRequestURI(),
                errors
        );


        return ResponseEntity.status(status)
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex,
            HttpServletRequest request
    ) {

        log.error("Request failed at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse response = new ErrorResponse(
                status.value(),
                "Internal server error",
                Instant.now(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(status)
                .body(response);
    }
}
