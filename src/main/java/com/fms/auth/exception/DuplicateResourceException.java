package com.fms.auth.exception;

/**
 * 409 CONFLICT
 * Thrown when a mobile number or email already exists in the database.
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
