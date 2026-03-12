package com.FacileApplication.FacileApplication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * DTO (Data Transfer Object) - represents the JSON body sent by the client.
 *
 * Validation annotations here ensure:
 *   - No field is null or blank
 *   - Email format is valid
 *   - Mobile number follows international format with country code
 */
@Data  // Lombok: generates getters, setters, toString
public class UserRequestDTO {

    @NotBlank(message = "User name is required")
    private String userName;

    @NotBlank(message = "Designation is required")
    private String designation;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotNull(message = "userId must not be null")
    @Min(value = 1, message = "userId must be positive")
    private Long userId;

    private String roleId;

    private String roleName;

    private Integer createdBy;

    private String password;

    private Integer status;

    private Integer designationId;

    /**
     * Mobile number must include country code.
     * Accepted formats:9876543210
     */
    @NotBlank(message = "Mobile number is required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Mobile number should be 10 digit number. Example: 9876543210"
    )
    private String mobileNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;
}