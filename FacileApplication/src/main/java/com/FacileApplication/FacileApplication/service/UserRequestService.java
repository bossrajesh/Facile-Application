package com.FacileApplication.FacileApplication.service;

import com.FacileApplication.FacileApplication.dto.UserRequestDTO;
import com.FacileApplication.FacileApplication.exception.MobileNumberAlreadyExistsException;
import com.FacileApplication.FacileApplication.model.UserRequest;
import com.FacileApplication.FacileApplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service layer - contains all business logic.
 *
 * Responsibilities:
 *   1. Check for duplicate mobile numbers
 *   2. Map DTO -> Entity
 *   3. Save to database
 *   4. Return saved entity
 */
@Service
@RequiredArgsConstructor  // Lombok: injects dependencies via constructor (preferred over @Autowired)
@Slf4j                    // Lombok: provides log.info(), log.error(), etc.
public class UserRequestService {

    private final UserRepository UserRepository;

    /**
     * Saves a new user request.
     *
     * Steps:
     *   1. Check if mobile number already exists -> throw error if yes
     *   2. Convert DTO to Entity
     *   3. Save entity to database
     *   4. Return saved entity
     *
     * @param dto - incoming request data from the controller
     * @return saved UserRequest entity (with generated ID and timestamp)
     */
    public UserRequest saveUserRequest(UserRequestDTO dto) {

        log.info("Processing user request for mobile: {}", dto.getMobileNumber());

        // Step 1: Check for duplicate mobile number
        if (UserRepository.existsByMobileNumber(dto.getMobileNumber())) {
            log.warn("Duplicate mobile number attempt: {}", dto.getMobileNumber());
            throw new MobileNumberAlreadyExistsException(dto.getMobileNumber());
        }
        log.info("Processing user request: {}", dto);

        // Step 2: Map DTO to Entity using builder pattern
        UserRequest userRequest = UserRequest.builder()
                .userName(dto.getUserName().trim())
                .designation(dto.getDesignation().trim())
                .companyName(dto.getCompanyName().trim())
                .mobileNumber(dto.getMobileNumber().trim())
                .email(dto.getEmail().trim().toLowerCase())
                .userId(dto.getUserId())
                .designationId(7)
                .roleId("fms00020")
                .createdBy(1169)
                .roleName("Elibrary")
                .status(1)
                .password("JDJ5JDEwJFBiSjF2TzRpdE1wSDdQbzJGN2RuaC5TV1pHWlJPaWJRaUM3MExCdjVhdzNzUTVYTzI1OVhl")
                .build();

        // Step 3: Save to DB (INSERT query executed here)
        UserRequest savedRequest = UserRepository.save(userRequest);
        // get last inserted id
        Long lastInsertedId = savedRequest.getId();

        // update user_id column
        savedRequest.setUserId(lastInsertedId);
        UserRepository.save(savedRequest);

        log.info("User request saved successfully with ID: {}", savedRequest.getId());

        // Step 4: Return saved entity
        return savedRequest;
    }
}