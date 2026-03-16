package com.fms.auth.exception;

/**
 * 429 TOO MANY REQUESTS
 * Thrown when user exceeds maximum OTP attempts (3 attempts).
 */
public class OtpMaxAttemptsException extends RuntimeException {
    public OtpMaxAttemptsException(String message) {
        super(message);
    }
}
