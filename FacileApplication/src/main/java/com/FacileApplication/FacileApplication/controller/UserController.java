package com.FacileApplication.FacileApplication.controller;

import com.FacileApplication.FacileApplication.dto.UserRequestDTO;
import com.FacileApplication.FacileApplication.model.UserRequest;
import com.FacileApplication.FacileApplication.response.ApiResponse;
import com.FacileApplication.FacileApplication.service.UserRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller - handles incoming HTTP requests.
 *
 * Base URL: /api/v1/user-requests
 *
 * @RestController  -> marks this as a REST endpoint (returns JSON automatically)
 * @RequestMapping  -> sets the base URL path
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/user-requests")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserRequestService userRequestService;

    /**
     * POST /api/v1/user-requests
     *
     * Captures user request data and saves it to the database.
     *
     * @Valid        -> triggers validation of all annotations in UserRequestDTO
     * @RequestBody  -> maps JSON request body to UserRequestDTO object
     *
     * Success Response (HTTP 201):
     * {
     *   "status": 201,
     *   "message": "User request submitted successfully",
     *   "data": {
     *     "id": 1,
     *     "userName": "John Doe",
     *     "designation": "Manager",
     *     "companyName": "Acme Corp",
     *     "mobileNumber": "+91-9876543210",
     *     "email": "john@example.com",
     *     "createdAt": "2024-01-15T10:30:00"
     *   }
     * }
     */


    @PostMapping
    public ResponseEntity<ApiResponse<UserRequest>> createUserRequest(
            @Valid @RequestBody UserRequestDTO userRequestDTO) {

        log.info("Received user request submission for: {}", userRequestDTO.getEmail());

        // Delegate business logic to service layer
        UserRequest savedRequest = userRequestService.saveUserRequest(userRequestDTO);

        // Build and return success response with HTTP 201 Created
        ApiResponse<UserRequest> response = ApiResponse.<UserRequest>builder()
                .status(HttpStatus.CREATED.value())
                .message("User request submitted successfully")
                .data(savedRequest)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}