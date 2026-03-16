package com.fms.auth.repository;

import com.fms.auth.entity.UserMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserMaster Repository
 * ----------------------
 * This is the "data access layer" — it talks directly to the
 * fms_user_master table in MySQL.
 *
 * Spring Data JPA auto-generates the SQL queries for us
 * based on the method names. No need to write raw SQL!
 */
@Repository
public interface UserMasterRepository extends JpaRepository<UserMaster, Long> {

    /**
     * Check if a mobile number is already registered.
     * SQL equivalent: SELECT COUNT(*) > 0 FROM fms_user_master WHERE mobile_number = ?
     */
    boolean existsByMobileNumber(String mobileNumber);

    /**
     * Check if an email is already registered.
     * SQL equivalent: SELECT COUNT(*) > 0 FROM fms_user_master WHERE email = ?
     */
    boolean existsByEmail(String email);

    /**
     * Find a user by their mobile number.
     * Returns Optional.empty() if not found — avoids NullPointerException.
     */
    Optional<UserMaster> findByMobileNumber(String mobileNumber);
}
