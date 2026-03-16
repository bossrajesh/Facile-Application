package com.fms.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Mobile OTP Entity
 * -----------------
 * This maps to the `fms_mobile_otp` table in MySQL.
 *
 * Think of this as a "scratch pad" where we temporarily store
 * the OTP we sent to the user's phone. Once verified, the OTP
 * is marked as used and should not be accepted again.
 *
 * OTP Lifecycle:
 *   1. User requests OTP  → new row inserted (is_used = false)
 *   2. User submits OTP   → row checked for validity & expiry
 *   3. OTP verified       → is_used = true, is_verified = true
 */
@Entity
@Table(name = "fms_mobile_otp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MobileOtp {

    /** Auto-generated primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "otp_id")
    private Long otpId;

    /** The mobile number the OTP was sent to */
    @Column(name = "mobile_number", nullable = false, length = 15)
    private String mobileNumber;

    /** The actual 6-digit OTP code */
    @Column(name = "otp_code", nullable = false, length = 10)
    private String otpCode;

    /** When the OTP expires — calculated as createdAt + 5 minutes */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Has this OTP already been used?
     * Once used, it cannot be reused even if still within expiry time.
     */
    @Column(name = "is_used", nullable = false)
    @Builder.Default
    private Boolean isUsed = false;

    /** Was the OTP successfully verified? */
    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = false;

    /** How many times the user attempted this OTP (max 3 attempts) */
    @Column(name = "attempt_count", nullable = false)
    @Builder.Default
    private Integer attemptCount = 0;

    /** Timestamp when OTP was generated */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
