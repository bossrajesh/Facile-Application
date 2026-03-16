package com.fms.auth.exception;

import com.fms.auth.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 * -------------------------
 * This is the "central error handling station" for the entire application.
 *
 * Without this, errors would return ugly stack traces.
 * With this, every error returns a clean, consistent JSON response.
 *
 * @RestControllerAdvice → intercepts exceptions thrown by any controller
 *
 * Flow:
 *   Exception thrown → GlobalExceptionHandler catches it
 *   → Returns structured JSON with the right HTTP status code
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles 409 CONFLICT
     * When: Mobile number or email already registered
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResource(DuplicateResourceException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    /**
     * Handles 404 NOT FOUND
     * When: Mobile number not found in fms_user_master
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    /**
     * Handles 400 BAD REQUEST
     * When: OTP is incorrect, expired, or already used
     */
    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidOtp(InvalidOtpException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    /**
     * Handles 429 TOO MANY REQUESTS
     * When: User exceeds 3 OTP attempts
     */
    @ExceptionHandler(OtpMaxAttemptsException.class)
    public ResponseEntity<ApiResponse<Object>> handleOtpMaxAttempts(OtpMaxAttemptsException ex) {
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ApiResponse.error(HttpStatus.TOO_MANY_REQUESTS.value(), ex.getMessage()));
    }

    /**
     * Handles 400 BAD REQUEST — Validation Errors
     * When: @NotBlank, @Email, @Pattern validations fail on request body
     *
     * Example: user submits empty mobile number or invalid email
     * Returns all validation errors at once so user knows what to fix.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed. Please check the input fields.")
                .data(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles 500 INTERNAL SERVER ERROR
     * When: Any unexpected error occurs
     * Catches all other exceptions as a safety net.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An unexpected error occurred. Please try again later."
                ));
    }
}
