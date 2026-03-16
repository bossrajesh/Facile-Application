package com.fms.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * OTP Verification Request DTO
 * -----------------------------
 * The user provides the OTP they received on their phone.
 * Think of it as entering the code in the "Enter OTP" screen.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpVerifyRequest {

    @NotBlank(message = "Mobile number is required")
    @Pattern(
        regexp = "^(\\+91|0)?[6-9][0-9]{9}$",
        message = "Please provide a valid Indian mobile number"
    )
    private String mobileNumber;

    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be exactly 6 digits")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP must contain only digits")
    private String otpCode;
}
