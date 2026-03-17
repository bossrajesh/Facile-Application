package com.fms.auth.dto.response;

import lombok.*;

/**
 * Ping4SMS API Response DTO
 * --------------------------
 * Maps the JSON response returned by the Ping4SMS API after sending an SMS.
 *
 * Success response from Ping4SMS looks like:
 * {
 *   "message": "SMS Submitted Successfully",
 *   "status": "success",
 *   "id": 32961147
 * }
 *
 * Error response:
 * {
 *   "message": "Invalid API Key",
 *   "status": "error",
 *   "id": null
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsResponse {

    /** Human-readable message from Ping4SMS */
    private String message;
    private Long otpCode;
    /** "success" or "error" */
    private String status;

    /** Delivery ID returned on success — useful for delivery reports */
    private Long id;

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(this.status);
    }
}
