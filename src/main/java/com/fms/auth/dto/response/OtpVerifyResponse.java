package com.fms.auth.dto.response;

import lombok.*;

/**
 * OTP Verification Response DTO
 * ------------------------------
 * Returned after successful OTP verification.
 * Includes the JWT token the frontend must save and send
 * with every subsequent API request.
 *
 * How the frontend uses this:
 *   1. User verifies OTP → gets jwtToken back
 *   2. Frontend stores token (e.g., in memory or secure storage)
 *   3. Every future API call includes: Authorization: Bearer <jwtToken>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpVerifyResponse {

    private String mobileNumber;
    private String username;

    /** JWT Token — use this for all authenticated API calls */
    private String jwtToken;

    /** Token type — always "Bearer" */
    @Builder.Default
    private String tokenType = "Bearer";

    /** Token validity in seconds (e.g., 86400 = 24 hours) */
    private Long expiresIn;

    private String message;
}
