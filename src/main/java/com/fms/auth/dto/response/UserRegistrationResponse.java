package com.fms.auth.dto.response;

import lombok.*;

/**
 * User Registration Response DTO
 * --------------------------------
 * Returned after a successful registration.
 * Only exposes safe fields — no sensitive data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationResponse {

    private Long userId;
    private String username;
    private String email;
    private String mobileNumber;
    private Integer status;
    private String message;
}
