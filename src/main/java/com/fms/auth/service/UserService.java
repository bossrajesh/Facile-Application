package com.fms.auth.service;

import com.fms.auth.dto.request.UserRegistrationRequest;
import com.fms.auth.dto.response.UserRegistrationResponse;

/**
 * User Service Interface
 * ----------------------
 * Defines what operations the User service can perform.
 *
 * Using an interface is good practice — it separates
 * "what to do" (interface) from "how to do it" (implementation).
 * Makes it easier to swap implementations or write unit tests.
 */
public interface UserService {

    /**
     * Register a new user.
     *
     * Steps:
     *   1. Check if mobile number already exists → throw error if yes
     *   2. Check if email already exists → throw error if yes
     *   3. Save user to fms_user_master table
     *   4. Return registration response
     *
     * @param request - user registration form data
     * @return registration response with user details
     */
    UserRegistrationResponse registerUser(UserRegistrationRequest request);
}
