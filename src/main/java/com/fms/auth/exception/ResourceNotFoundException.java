package com.fms.auth.exception;

/**
 * 404 NOT FOUND
 * Thrown when a mobile number does not exist in fms_user_master.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
