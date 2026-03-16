package com.fms.auth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * OTP Utility
 * -----------
 * Handles generating secure OTP codes.
 *
 * Uses SecureRandom (not Random) — SecureRandom is
 * cryptographically strong, meaning it's much harder to predict
 * compared to regular Random. Important for security!
 */
@Component
public class OtpUtil {

    @Value("${app.otp.length:6}")
    private int otpLength;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generate a random numeric OTP.
     *
     * How it works:
     *   - For a 6-digit OTP: generates a number between 100000 and 999999
     *   - SecureRandom ensures the number is unpredictable
     *
     * @return OTP as a zero-padded string (e.g., "045231")
     */
    public String generateOtp() {
        int min = (int) Math.pow(10, otpLength - 1); // 100000
        int max = (int) Math.pow(10, otpLength) - 1;  // 999999
        int otp = min + SECURE_RANDOM.nextInt(max - min + 1);
        return String.format("%0" + otpLength + "d", otp);
    }
}
