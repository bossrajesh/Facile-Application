package com.fms.auth.service.impl;

import com.fms.auth.dto.request.UserRegistrationRequest;
import com.fms.auth.dto.response.UserRegistrationResponse;
import com.fms.auth.entity.UserMaster;
import com.fms.auth.exception.DuplicateResourceException;
import com.fms.auth.repository.UserMasterRepository;
import com.fms.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User Service Implementation
 * ----------------------------
 * Contains the actual business logic for user registration.
 *
 * @Service → Spring manages this as a service bean
 * @Transactional → if something fails mid-way, the DB is rolled back
 * @RequiredArgsConstructor → Lombok auto-generates constructor for final fields
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserMasterRepository userMasterRepository;

    /**
     * Register a new user.
     *
     * VALIDATION CHECKS (in order):
     *   ① Mobile number must not already exist
     *   ② Email must not already exist
     *
     * If all checks pass → save user → return success response
     */
    @Override
    @Transactional
    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {

        log.info("Attempting to register user with mobile: {}", maskMobile(request.getMobileNumber()));

        // ─── Step 1: Check if mobile number already exists ───────────────
        // If someone already registered with this number, reject with 409 Conflict
        if (userMasterRepository.existsByMobileNumber(request.getMobileNumber())) {
            log.warn("Registration failed - mobile already exists: {}", maskMobile(request.getMobileNumber()));
            throw new DuplicateResourceException("Mobile Number already exists");
        }

        // ─── Step 2: Check if email already exists ────────────────────────
        if (userMasterRepository.existsByEmail(request.getEmail().toLowerCase())) {
            log.warn("Registration failed - email already exists: {}", maskEmail(request.getEmail()));
            throw new DuplicateResourceException("Email address already exists");
        }

        System.out.println("Show userid %d "+ request.getUserId());

        // ─── Step 3: Build and save the new user ─────────────────────────
        UserMaster newUser = UserMaster.builder()
                .username(request.getUsername().trim())
                .email(request.getEmail().toLowerCase().trim())
                .mobileNumber(request.getMobileNumber().trim())
                .userId(2345L)
                .companyName(request.getCompanyName().trim())
                .designation(request.getDesignation().trim())
                .designationId(7)
                .roleId("fms00020")
                .createdBy(1169)
                .roleName("Elibrary")
                .status(1)
                .password("JDJ5JDEwJFBiSjF2TzRpdE1wSDdQbzJGN2RuaC5TV1pHWlJPaWJRaUM3MExCdjVhdzNzUTVYTzI1OVhl")
                .build();

        UserMaster savedUser = userMasterRepository.save(newUser);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // Get the last inserted id and set it to userid
        Long lastInsertedId = savedUser.getId();

        // Updating the userid with last inserted record
        savedUser.setUserId(lastInsertedId);
        userMasterRepository.save(savedUser);

        // ─── Step 4: Build and return the response ────────────────────────
        return UserRegistrationResponse.builder()
                .userId(savedUser.getUserId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .mobileNumber(savedUser.getMobileNumber())
                .status(savedUser.getStatus())
                .message("User registered successfully. Please verify your mobile number via OTP.")
                .build();
    }

    // ─── Private Helpers: mask sensitive data in logs ────────────────────────

    private String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 4) return "****";
        return "******" + mobile.substring(mobile.length() - 4);
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "****";
        return email.substring(0, 2) + "****" + email.substring(email.indexOf("@"));
    }
}
