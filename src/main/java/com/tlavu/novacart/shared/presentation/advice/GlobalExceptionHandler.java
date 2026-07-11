package com.tlavu.novacart.shared.presentation.advice;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.tlavu.novacart.shared.application.exception.code.contract.ErrorCode;
import com.tlavu.novacart.shared.application.exception.code.global.GlobalErrorCode;
import com.tlavu.novacart.shared.application.exception.common.ConflictException;
import com.tlavu.novacart.shared.application.exception.common.InvalidSortFieldException;
import com.tlavu.novacart.shared.application.exception.common.ResourceNotFoundException;
import com.tlavu.novacart.shared.application.exception.common.InvalidInputException;
import com.tlavu.novacart.shared.presentation.dto.response.ApiResponse;
import com.tlavu.novacart.shared.presentation.dto.error.ApiError;
import com.tlavu.novacart.shared.presentation.dto.error.FieldErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
                GlobalErrorCode.VALIDATION_FAILED,
                GlobalErrorCode.VALIDATION_FAILED.getDefaultMessage(),
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

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(
            InvalidInputException ex,
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {

        String message = "Malformed JSON request";

        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException ife && !ife.getPath().isEmpty()) {
            String fieldName = ife.getPath()
                    .stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("."));

            String invalidValue = String.valueOf(ife.getValue());

            String expectedType = ife.getTargetType().getSimpleName();

            message = "Invalid value '%s' for field '%s', expected type: %s"
                    .formatted(invalidValue, fieldName, expectedType);
        }

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                GlobalErrorCode.VALIDATION_FAILED,
                message,
                request,
                null,
                ex
        );
    }

    @ExceptionHandler(InvalidSortFieldException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidSortField(
            InvalidSortFieldException ex,
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

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(
            NoResourceFoundException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                GlobalErrorCode.RESOURCE_NOT_FOUND,
                GlobalErrorCode.RESOURCE_NOT_FOUND.getDefaultMessage(),
                request,
                null,
                ex
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        String message = "Parameter '%s' must be of type %s".formatted(
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        );

        List<FieldErrorResponse> errors = List.of(
                new FieldErrorResponse(ex.getName(), message)
        );

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                GlobalErrorCode.INVALID_PARAMETER_TYPE,
                GlobalErrorCode.INVALID_PARAMETER_TYPE.getDefaultMessage(),
                request,
                errors,
                ex
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {
        String message = "Method '%s' is not supported for this endpoint".formatted(ex.getMethod());

        return buildErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                GlobalErrorCode.METHOD_NOT_ALLOWED,
                message,
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
                GlobalErrorCode.INTERNAL_SERVER_ERROR,
                GlobalErrorCode.INTERNAL_SERVER_ERROR.getDefaultMessage(),
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
