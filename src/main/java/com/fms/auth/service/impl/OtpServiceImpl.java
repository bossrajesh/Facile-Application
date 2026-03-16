package com.fms.auth.service.impl;

import com.fms.auth.dto.request.OtpSendRequest;
import com.fms.auth.dto.request.OtpVerifyRequest;
import com.fms.auth.dto.response.OtpSendResponse;
import com.fms.auth.dto.response.OtpVerifyResponse;
import com.fms.auth.entity.MobileOtp;
import com.fms.auth.entity.UserMaster;
import com.fms.auth.exception.InvalidOtpException;
import com.fms.auth.exception.OtpMaxAttemptsException;
import com.fms.auth.exception.ResourceNotFoundException;
import com.fms.auth.repository.MobileOtpRepository;
import com.fms.auth.repository.UserMasterRepository;
import com.fms.auth.service.OtpService;
import com.fms.auth.service.SmsService;
import com.fms.auth.util.JwtUtil;
import com.fms.auth.util.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpServiceImpl.class);
    private static final int MAX_OTP_ATTEMPTS = 3;

    private final UserMasterRepository userMasterRepository;
    private final MobileOtpRepository mobileOtpRepository;
    private final SmsService smsService;         // ← Ping4SMS injected here
    private final OtpUtil otpUtil;
    private final JwtUtil jwtUtil;

    @Value("${app.otp.expiry-minutes:5}")
    private int otpExpiryMinutes;

    // ══════════════════════════════════════════════════════════════════════════
    // SEND OTP  — @Transactional is fine here, no exceptions after saves
    // ══════════════════════════════════════════════════════════════════════════
    @Override
    @Transactional
    public OtpSendResponse sendOtp(OtpSendRequest request) {

        String mobileNumber = request.getMobileNumber().trim();
        log.info("OTP requested for mobile: {}", maskMobile(mobileNumber));

        userMasterRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> {
                    log.warn("OTP request failed - mobile not found: {}", maskMobile(mobileNumber));
                    return new ResourceNotFoundException(
                            "Mobile number does not exist. Please register first.");
                });

        mobileOtpRepository.invalidatePreviousOtps(mobileNumber);

        String otpCode = otpUtil.generateOtp();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(otpExpiryMinutes);

        MobileOtp mobileOtp = MobileOtp.builder()
                .mobileNumber(mobileNumber)
                .otpCode(otpCode)
                .expiresAt(expiresAt)
                .isUsed(false)
                .isVerified(false)
                .attemptCount(0)
                .build();

        mobileOtpRepository.save(mobileOtp);
        log.info("OTP generated and saved for mobile: {}", maskMobile(mobileNumber));

        // ─── Step 5: Send OTP via Ping4SMS ────────────────────────────────
        // SmsService → Ping4SmsSmsServiceImpl → HTTP GET to pingsms.in/api/sendsms
        // If sending fails, the exception bubbles up and the DB insert is rolled back
        smsService.sendOtpSms(mobileNumber, otpCode);

        return OtpSendResponse.builder()
                .mobileNumber(mobileNumber)
                .expiresIn("OTP is valid for " + otpExpiryMinutes + " minutes")
                .message("OTP sent successfully to your registered mobile number.")
                .build();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // VERIFY OTP
    //
    // ⚠️  NO @Transactional on this method intentionally.
    //
    // Why removed:
    //   If this method had @Transactional, ALL repository calls would join
    //   that single outer transaction. When an exception is thrown (wrong OTP,
    //   expired OTP), Spring rolls back the entire outer transaction — undoing
    //   incrementAttemptCount() and markAsUsed() even though they already ran.
    //
    //   By removing @Transactional here, each repository method runs in its
    //   own REQUIRES_NEW transaction and commits immediately to MySQL.
    //   Exceptions thrown after them have no effect on what's already committed.
    //
    //   The success path (Step 6 & 7) has its own @Transactional via save()
    //   which is fine — no exceptions are thrown after those saves.
    // ══════════════════════════════════════════════════════════════════════════
    @Override
    public OtpVerifyResponse verifyOtp(OtpVerifyRequest request) {

        String mobileNumber = request.getMobileNumber().trim();
        String submittedOtp = request.getOtpCode().trim();

        log.info("OTP verification attempt for mobile: {}", maskMobile(mobileNumber));

        // ─── Step 1: Find latest active OTP ───────────────────────────────
        MobileOtp mobileOtp = mobileOtpRepository
                .findTopByMobileNumberAndIsUsedFalseAndIsVerifiedFalseOrderByCreatedAtDesc(mobileNumber)
                .orElseThrow(() -> new InvalidOtpException(
                        "No active OTP found for this mobile number. Please request a new OTP."));

        Long otpId = mobileOtp.getOtpId();

        // ─── Step 2: Check if already at max attempts ─────────────────────
        if (mobileOtp.getAttemptCount() >= MAX_OTP_ATTEMPTS) {
            mobileOtpRepository.markAsUsed(otpId); // REQUIRES_NEW → commits immediately ✅
            log.warn("OTP max attempts exceeded for mobile: {}", maskMobile(mobileNumber));
            throw new OtpMaxAttemptsException(
                    "Maximum OTP attempts exceeded. Please request a new OTP.");
        }

        // ─── Step 3: Increment attempt count ──────────────────────────────
        // REQUIRES_NEW → opens its own transaction, commits to MySQL right now.
        // Even if we throw an exception below, this UPDATE is already in the DB.
        mobileOtpRepository.incrementAttemptCount(otpId); // ✅ always persists
        int newAttemptCount = mobileOtp.getAttemptCount() + 1;

        // ─── Step 4: Check expiry ──────────────────────────────────────────
        if (LocalDateTime.now().isAfter(mobileOtp.getExpiresAt())) {
            mobileOtpRepository.markAsUsed(otpId); // REQUIRES_NEW → commits immediately ✅
            log.warn("OTP expired for mobile: {}", maskMobile(mobileNumber));
            throw new InvalidOtpException(
                    "OTP has expired. Please request a new OTP.");
        }

        // ─── Step 5: Check if OTP matches ─────────────────────────────────
        if (!mobileOtp.getOtpCode().equals(submittedOtp)) {
            int remainingAttempts = MAX_OTP_ATTEMPTS - newAttemptCount;
            log.warn("Invalid OTP for mobile: {} | Attempt: {} | Remaining: {}",
                    maskMobile(mobileNumber), newAttemptCount, remainingAttempts);

            if (remainingAttempts <= 0) {
                mobileOtpRepository.markAsUsed(otpId); // REQUIRES_NEW → commits immediately ✅
                throw new OtpMaxAttemptsException(
                        "Maximum OTP attempts exceeded. Please request a new OTP.");
            }

            throw new InvalidOtpException(
                    "Invalid OTP. You have " + remainingAttempts + " attempt(s) remaining.");
        }

        // ─── Step 6: OTP is correct — mark as used and verified ──────────
        mobileOtp.setIsUsed(true);
        mobileOtp.setIsVerified(true);
        mobileOtpRepository.save(mobileOtp); // no exception after this ✅

        // ─── Step 7: Update user's mobile verification status ────────────
        UserMaster user = userMasterRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mobile number does not exist."));

//        user.setIsMobileVerified(true);
//        userMasterRepository.save(user);
        log.info("Mobile verified successfully for user ID: {}", user.getUserId());

        // ─── Step 8: Generate JWT token ───────────────────────────────────
        String jwtToken = jwtUtil.generateToken(mobileNumber, user.getUsername());
        log.info("JWT token generated for mobile: {}", maskMobile(mobileNumber));

        return OtpVerifyResponse.builder()
                .mobileNumber(mobileNumber)
                .username(user.getUsername())
                .jwtToken(jwtToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationInSeconds())
                .message("OTP verified successfully. You are now logged in.")
                .build();
    }

    private String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 4) return "****";
        return "******" + mobile.substring(mobile.length() - 4);
    }
}
