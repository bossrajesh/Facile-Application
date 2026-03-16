package com.fms.auth.repository;

import com.fms.auth.entity.MobileOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * MobileOtp Repository
 * ---------------------
 *
 * KEY FIX — Propagation.REQUIRES_NEW:
 * -------------------------------------
 * Problem: verifyOtp() in the service is @Transactional.
 * When we call incrementAttemptCount() or markAsUsed() inside it,
 * Spring sees an active transaction already exists and JOINS it
 * (default Propagation.REQUIRED). So both the repository update
 * AND the outer method share the same transaction.
 *
 * When an exception is thrown in verifyOtp(), Spring rolls back
 * the ENTIRE shared transaction — including the UPDATE that already ran.
 * MySQL never commits the attempt_count change.
 *
 * Fix: Propagation.REQUIRES_NEW
 * → Forces Spring to SUSPEND the outer transaction and open a
 *   brand new, independent one for just this query.
 * → This new transaction commits to MySQL immediately on its own.
 * → When the outer transaction rolls back, this one is already done.
 *
 * Timeline:
 *   [Outer TX starts]
 *     → incrementAttemptCount() called
 *       [Inner TX starts]  ← REQUIRES_NEW suspends outer
 *         UPDATE attempt_count = attempt_count + 1  ✅ COMMIT
 *       [Inner TX committed]
 *     → throw InvalidOtpException()
 *   [Outer TX rolls back]  ← too late, inner already committed
 */
@Repository
public interface MobileOtpRepository extends JpaRepository<MobileOtp, Long> {

    Optional<MobileOtp> findTopByMobileNumberAndIsUsedFalseAndIsVerifiedFalseOrderByCreatedAtDesc(
            String mobileNumber);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query("UPDATE MobileOtp m SET m.isUsed = true WHERE m.mobileNumber = :mobileNumber AND m.isUsed = false")
    void invalidatePreviousOtps(@Param("mobileNumber") String mobileNumber);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query("UPDATE MobileOtp m SET m.attemptCount = m.attemptCount + 1 WHERE m.otpId = :otpId")
    void incrementAttemptCount(@Param("otpId") Long otpId);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query("UPDATE MobileOtp m SET m.isUsed = true WHERE m.otpId = :otpId")
    void markAsUsed(@Param("otpId") Long otpId);
}
