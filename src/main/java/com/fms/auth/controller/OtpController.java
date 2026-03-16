package com.fms.auth.controller;

import com.fms.auth.dto.request.OtpSendRequest;
import com.fms.auth.dto.request.OtpVerifyRequest;
import com.fms.auth.dto.response.ApiResponse;
import com.fms.auth.dto.response.OtpSendResponse;
import com.fms.auth.dto.response.OtpVerifyResponse;
import com.fms.auth.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * OTP Controller
 * ---------------
 * Exposes HTTP REST endpoints for Mobile OTP operations.
 *
 * Endpoints:
 *   POST /api/v1/auth/otp/send    → Request an OTP
 *   POST /api/v1/auth/otp/verify  → Verify OTP and get JWT
 */
@RestController
@RequestMapping("/api/v1/auth/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    /**
     * POST /api/v1/auth/otp/send
     * ---------------------------
     * Request an OTP for the given mobile number.
     *
     * Request Body:
     * {
     *   "mobileNumber": "9876543210"
     * }
     *
     * Success Response (200 OK):
     * {
     *   "success": true,
     *   "httpStatus": 200,
     *   "message": "OTP sent successfully",
     *   "data": {
     *     "mobileNumber": "9876543210",
     *     "otpCode": "482910",    ← REMOVE IN PRODUCTION
     *     "expiresIn": "OTP is valid for 5 minutes"
     *   }
     * }
     *
     * Error Response (404 Not Found):
     * {
     *   "success": false,
     *   "httpStatus": 404,
     *   "message": "Mobile number does not exist. Please register first."
     * }
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<OtpSendResponse>> sendOtp(
            @Valid @RequestBody OtpSendRequest request) {

        OtpSendResponse response = otpService.sendOtp(request);

        return ResponseEntity
                .status(HttpStatus.OK)  // 200 OK
                .body(ApiResponse.success(
                        HttpStatus.OK.value(),
                        "OTP sent successfully to registered mobile number",
                        response
                ));
    }

    /**
     * POST /api/v1/auth/otp/verify
     * -----------------------------
     * Verify the OTP and receive a JWT token.
     *
     * Request Body:
     * {
     *   "mobileNumber": "9876543210",
     *   "otpCode": "482910"
     * }
     *
     * Success Response (200 OK):
     * {
     *   "success": true,
     *   "httpStatus": 200,
     *   "message": "OTP verified successfully. You are now logged in.",
     *   "data": {
     *     "mobileNumber": "9876543210",
     *     "username": "John Doe",
     *     "jwtToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *     "tokenType": "Bearer",
     *     "expiresIn": 86400
     *   }
     * }
     *
     * Error Response (400 Bad Request):
     * {
     *   "success": false,
     *   "httpStatus": 400,
     *   "message": "Invalid OTP. You have 2 attempt(s) remaining."
     * }
     *
     * Error Response (400 Bad Request - Expired):
     * {
     *   "success": false,
     *   "httpStatus": 400,
     *   "message": "OTP has expired. Please request a new OTP."
     * }
     *
     * Error Response (429 Too Many Requests):
     * {
     *   "success": false,
     *   "httpStatus": 429,
     *   "message": "Maximum OTP attempts exceeded. Please request a new OTP."
     * }
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<OtpVerifyResponse>> verifyOtp(
            @Valid @RequestBody OtpVerifyRequest request) {

        OtpVerifyResponse response = otpService.verifyOtp(request);

        return ResponseEntity
                .status(HttpStatus.OK)  // 200 OK
                .body(ApiResponse.success(
                        HttpStatus.OK.value(),
                        "OTP verified successfully. You are now logged in.",
                        response
                ));
    }
}
