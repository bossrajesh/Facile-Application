package com.fms.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Standard API Response Wrapper
 * --------------------------------
 * Every API response from this application follows this structure.
 *
 * Think of this as a "standard envelope" — every response is wrapped
 * in the same format so the frontend always knows what to expect.
 *
 * Example Success:
 * {
 *   "success": true,
 *   "httpStatus": 200,
 *   "message": "User registered successfully",
 *   "data": { ... },
 *   "timestamp": "2024-01-15T10:30:00"
 * }
 *
 * Example Error:
 * {
 *   "success": false,
 *   "httpStatus": 409,
 *   "message": "Mobile Number already exists",
 *   "data": null,
 *   "timestamp": "2024-01-15T10:30:00"
 * }
 *
 * @JsonInclude(NON_NULL) → fields with null value are hidden from JSON output
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /** Was the request successful? */
    private boolean success;

    /** HTTP status code (200, 201, 400, 404, 409, 500, etc.) */
    private int httpStatus;

    /** Human-readable message explaining the result */
    private String message;

    /** The actual data payload (can be null for errors) */
    private T data;

    /** When this response was generated */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    // ─── Static Factory Methods ──────────────────────────────────────

    /** Quick helper to build a success response */
    public static <T> ApiResponse<T> success(int httpStatus, String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .httpStatus(httpStatus)
                .message(message)
                .data(data)
                .build();
    }

    /** Quick helper to build an error response */
    public static <T> ApiResponse<T> error(int httpStatus, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .httpStatus(httpStatus)
                .message(message)
                .build();
    }
}
