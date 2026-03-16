package com.fms.auth.exception;

/**
 * 400 BAD REQUEST
 * Thrown when OTP is wrong, expired, or already used.
 */
public class InvalidOtpException extends RuntimeException {
    public InvalidOtpException(String message) {
        super(message);
    }
}
