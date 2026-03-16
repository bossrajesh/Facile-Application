package com.fms.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * OTP Send Request DTO
 * ---------------------
 * The user provides their mobile number to request an OTP.
 * Think of it as pressing "Send OTP" on the login screen.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpSendRequest {

    @NotBlank(message = "Mobile number is required")
    @Pattern(
        regexp = "^(\\+91|0)?[6-9][0-9]{9}$",
        message = "Please provide a valid Indian mobile number"
    )
    private String mobileNumber;
}
