package com.fms.auth.dto.response;

import lombok.*;

/**
 * OTP Send Response DTO
 * ----------------------
 * Returned after OTP is generated and "sent".
 *
 * NOTE: In a real production system, you would NOT return the
 * OTP in the response — it would be sent via SMS.
 * For this demo/dev environment, we include it so you can test
 * without an actual SMS gateway.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpSendResponse {

    private String mobileNumber;

    /**
     * The OTP code.
     * ⚠️  REMOVE THIS IN PRODUCTION — use SMS gateway instead.
     * Included here for development/testing convenience only.
     */
    private String otpCode;

    /** When the OTP will expire (e.g., "OTP valid for 5 minutes") */
    private String expiresIn;

    private String message;
}
