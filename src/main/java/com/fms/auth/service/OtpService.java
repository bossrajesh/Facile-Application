package com.fms.auth.service;

import com.fms.auth.dto.request.OtpSendRequest;
import com.fms.auth.dto.request.OtpVerifyRequest;
import com.fms.auth.dto.response.OtpSendResponse;
import com.fms.auth.dto.response.OtpVerifyResponse;

/**
 * OTP Service Interface
 * ----------------------
 * Defines the OTP-related operations.
 */
public interface OtpService {

    /**
     * Send OTP to mobile number.
     *
     * Steps:
     *   1. Check if mobile number exists in fms_user_master → throw error if not
     *   2. Invalidate any old unused OTPs for this number
     *   3. Generate a new 6-digit OTP
     *   4. Save OTP to fms_mobile_otp table with expiry time
     *   5. "Send" OTP (return in response for demo; use SMS gateway in production)
     *
     * @param request - contains mobile number
     * @return OTP details (including OTP code for demo purposes)
     */
    OtpSendResponse sendOtp(OtpSendRequest request);

    /**
     * Verify OTP entered by user.
     *
     * Steps:
     *   1. Find the latest unused OTP for this mobile number
     *   2. Check attempt count — reject if > 3 attempts
     *   3. Check if OTP has expired
     *   4. Check if OTP code matches
     *   5. Mark OTP as used and verified
     *   6. Mark user's mobile as verified in fms_user_master
     *   7. Generate and return JWT token
     *
     * @param request - contains mobile number and OTP code
     * @return JWT token on success
     */
    OtpVerifyResponse verifyOtp(OtpVerifyRequest request);
}
