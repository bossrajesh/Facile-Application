package com.FacileApplication.FacileApplication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration
 *
 * Since this is a public API (no login required), we:
 *   1. Disable CSRF (not needed for stateless REST APIs)
 *   2. Permit ALL incoming requests without authentication
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Disable CSRF — not needed for stateless REST APIs

        http.csrf(AbstractHttpConfigurer::disable)

            // Allow all requests without authentication
            .authorizeHttpRequests(auth ->
                    auth.requestMatchers("/api/v1/**").permitAll()

            );

        return http.build();
    }
}