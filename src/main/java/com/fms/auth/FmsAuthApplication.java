package com.fms.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * FMS Auth Application
 * ---------------------
 * Entry point for the Spring Boot application.
 * Handles User Registration and Mobile OTP Verification.
 */
@SpringBootApplication
public class FmsAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(FmsAuthApplication.class, args);
    }
}
