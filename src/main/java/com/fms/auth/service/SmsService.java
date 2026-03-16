package com.fms.auth.service;

/**
 * SMS Service Interface
 * ----------------------
 * Defines the contract for sending SMS messages.
 *
 * Using an interface here means we can easily swap
 * Ping4SMS for another provider (Twilio, MSG91, etc.)
 * in the future by just writing a new implementation —
 * no changes needed in OtpServiceImpl.
 */
public interface SmsService {

    /**
     * Send an OTP SMS to the given mobile number.
     *
     * @param mobileNumber  - 10-digit Indian mobile number
     * @param otpCode       - 6-digit OTP to include in the message
     */
    void sendOtpSms(String mobileNumber, String otpCode);
}
