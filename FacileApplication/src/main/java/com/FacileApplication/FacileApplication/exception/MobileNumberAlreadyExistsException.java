package com.FacileApplication.FacileApplication.exception;

/**
 * Custom exception thrown when a mobile number already exists in the database.
 * Extends RuntimeException so it doesn't need to be declared in method signatures.
 */
public class MobileNumberAlreadyExistsException extends RuntimeException {

    public MobileNumberAlreadyExistsException(String mobileNumber) {
        super("Mobile number already registered: " + mobileNumber);
    }
}