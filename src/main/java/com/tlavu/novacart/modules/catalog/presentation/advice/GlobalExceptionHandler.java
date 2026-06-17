package com.tlavu.novacart.modules.catalog.presentation.advice;

import com.tlavu.novacart.modules.catalog.application.exception.ConflictException;
import com.tlavu.novacart.modules.catalog.application.exception.ResourceNotFoundException;
import com.tlavu.novacart.modules.catalog.application.exception.ValidationException;
import com.tlavu.novacart.shared.dto.ApiResponse;
import com.tlavu.novacart.shared.exception.code.ErrorCode;
import com.tlavu.novacart.shared.dto.ApiError;
import com.tlavu.novacart.shared.dto.FieldErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(
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

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getErrorCode(),
                safeMessage(ex),
                request,
                null,
                ex
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(
            ConflictException ex,
            HttpServletRequest request
    ) {

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                ex.getErrorCode(),
                safeMessage(ex),
                request,
                null,
                ex
        );
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(
            ValidationException ex,
            HttpServletRequest request
    ) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getErrorCode(),
                safeMessage(ex),
                request,
                null,
                ex
        );
    }

    // Global fallback exception handler for all unhandled exceptions.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
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
    private ResponseEntity<ApiResponse<Void>> buildErrorResponse(
            HttpStatus status,
            ErrorCode errorCode,
            String message,
            HttpServletRequest request,
            List<FieldErrorResponse> errors,
            Exception ex
    ) {

        logByStatus(status, request, ex);

        ApiError error = new ApiError(
                status.value(),
                errorCode.getCode(),
                message,
                request.getRequestURI(),
                errors
        );

        return ResponseEntity.status(status)
                .body(ApiResponse.error(error));
    }

    // Helper
    private void logByStatus(
            HttpStatus status,
            HttpServletRequest request,
            Throwable ex
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

    private String safeMessage(Throwable ex) {
        if (ex == null || ex.getMessage() == null || ex.getMessage().isBlank()) {
            return "Unexpected error";
        }
        return ex.getMessage();
    }
}
