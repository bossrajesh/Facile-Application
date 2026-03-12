package com.FacileApplication.FacileApplication.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * Standard API response wrapper for all endpoints.
 *
 * Every response (success or error) will follow this structure:
 * {
 *   "status": 200,
 *   "message": "User request saved successfully",
 *   "data": { ... }   <- null for error responses
 * }
 *
 * @JsonInclude(NON_NULL) -> hides "data" field from JSON if it's null
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int status;
    private String message;
    private T data;

    // ------------------------------------
    // Static factory methods for convenience
    // ------------------------------------

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(int status, String message) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .build();
    }
}