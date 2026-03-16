package com.fms.auth.controller;

import com.fms.auth.dto.request.UserRegistrationRequest;
import com.fms.auth.dto.response.ApiResponse;
import com.fms.auth.dto.response.UserRegistrationResponse;
import com.fms.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * User Registration Controller
 * -----------------------------
 * Exposes HTTP REST endpoints for user registration.
 *
 * Base URL: /api/v1/auth
 *
 * Think of this as the "front door" of the application.
 * It receives requests from the outside world, passes them to
 * the service layer, and returns responses.
 *
 * @RestController → combines @Controller + @ResponseBody (auto-converts to JSON)
 * @RequestMapping → base path for all endpoints in this controller
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserRegistrationController {

    private final UserService userService;

    /**
     * POST /api/v1/auth/register
     * ----------------------------
     * Register a new user.
     *
     * Request Body:
     * {
     *   "username": "John Doe",
     *   "email": "john@example.com",
     *   "mobileNumber": "9876543210"
     * }
     *
     * Success Response (201 Created):
     * {
     *   "success": true,
     *   "httpStatus": 201,
     *   "message": "User registered successfully",
     *   "data": {
     *     "userId": 1,
     *     "username": "John Doe",
     *     "email": "john@example.com",
     *     "mobileNumber": "9876543210",
     *     "isMobileVerified": false,
     *     "status": "ACTIVE"
     *   }
     * }
     *
     * Error Response (409 Conflict):
     * {
     *   "success": false,
     *   "httpStatus": 409,
     *   "message": "Mobile Number already exists"
     * }
     *
     * @Valid → triggers bean validation on the request body
     * @RequestBody → Spring maps the JSON body to UserRegistrationRequest
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserRegistrationResponse>> registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {

        UserRegistrationResponse response = userService.registerUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)  // 201 Created
                .body(ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "User registered successfully",
                        response
                ));
    }
}
