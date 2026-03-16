package com.fms.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * User Registration Request DTO
 * ------------------------------
 * ROOT CAUSE FIX:
 * ---------------
 * Error was: { "userName": "Full Name is required" }
 *
 * Why it happened:
 *   - Request body was sending "userName" (capital N)
 *   - DTO field is named "username" (all lowercase)
 *   - Jackson couldn't map them → username = null → @NotBlank fired
 *
 * Fix applied:
 *   @JsonProperty("username") → canonical accepted name (lowercase)
 *   @JsonAlias("userName")    → also accepts camelCase variant
 *
 * Both of these now work in the request body:
 *   { "username": "John Doe" }   ✅
 *   { "userName": "John Doe" }   ✅
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationRequest {

    @JsonProperty("username")
    @JsonAlias("userName")
    @NotBlank(message = "Full Name is required")
    @Size(min = 2, max = 100, message = "Username must be between 2 and 100 characters")
    private String username;

    @JsonProperty("email")
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("companyName")
    @NotBlank(message = "Company Name is required")
    private String companyName;

    @JsonProperty("designation")
    @NotBlank(message = "Designation is required")
    private String designation;

    @JsonProperty("mobileNumber")
    @JsonAlias({"mobile_number", "mobile"})
    @NotBlank(message = "Mobile number is required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Please provide a valid Indian mobile number"
    )
    private String mobileNumber;
}
