package com.tlavu.novacart.modules.catalog.presentation.exception.handler;

import com.tlavu.novacart.modules.catalog.domain.exception.CategoryAlreadyExistsException;
import com.tlavu.novacart.modules.catalog.domain.exception.CategoryNotFoundException;
import com.tlavu.novacart.shared.exception.code.ErrorCode;
import com.tlavu.novacart.modules.catalog.presentation.exception.dto.ErrorResponse;
import com.tlavu.novacart.modules.catalog.presentation.exception.dto.FieldErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_FAILED,
                safeMessage(ex),
                request,
                null,
                ex
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex,
            HttpServletRequest request
    ) {

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                ErrorCode.CATEGORY_ALREADY_EXISTS,
                safeMessage(ex),
                request,
                null,
                ex
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        List<FieldErrorResponse> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorResponse(
                        error.getField(),
                        Objects.requireNonNullElse(error.getDefaultMessage(), "Invalid value")
                ))
                .toList();

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_FAILED,
                ErrorCode.VALIDATION_FAILED.getDefaultMessage(),
                request,
                errors,
                ex
        );
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFound(
            CategoryNotFoundException ex,
            HttpServletRequest request
    ) {

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ErrorCode.CATEGORY_NOT_FOUND,
                safeMessage(ex),
                request,
                null,
                ex
        );
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCategoryAlreadyExists(
            CategoryAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                ErrorCode.CATEGORY_ALREADY_EXISTS,
                safeMessage(ex),
                request,
                null,
                ex
        );
    }

    // Global fallback exception handler for all unhandled exceptions.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex,
            HttpServletRequest request
    ) {

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR.getDefaultMessage(),
                request,
                null,
                ex
        );
    }

    // Builds a standardized {@link ErrorResponse} wrapped in a {@link ResponseEntity}.
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status,
            ErrorCode errorCode,
            String message,
            HttpServletRequest request,
            List<FieldErrorResponse> errors,
            Exception ex
    ) {

        logByStatus(status, request, ex);

        ErrorResponse response = new ErrorResponse(
                status.value(),
                errorCode.getCode(),
                message,
                Instant.now(),
                request.getRequestURI(),
                errors
        );

        return ResponseEntity.status(status)
                .body(response);
    }

    // Helper
    private void logByStatus(
            HttpStatus status,
            HttpServletRequest request,
            Exception ex
    ) {

        String safeMessage = safeMessage(ex);

        if (status.is5xxServerError()) {
            log.error(
                    "Request failed at {}: {}",
                    request.getRequestURI(),
                    safeMessage,
                    ex
            );
        } else {
            log.warn(
                    "Request failed at {}: {}",
                    request.getRequestURI(),
                    safeMessage
            );
        }
    }

    private String safeMessage(Exception ex) {
        if (ex == null || ex.getMessage() == null) {
            return "Unexpected error";
        }
        return ex.getMessage();
    }
}
