package com.fms.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security Configuration
 * --------------------------------
 * Configures which API endpoints are public and which require authentication.
 *
 * In simple terms:
 *   - Registration and OTP endpoints → Anyone can call (no token needed)
 *   - Everything else → Must have a valid JWT token
 *
 * We use STATELESS sessions because JWTs are self-contained —
 * the server doesn't need to remember any session data.
 * Each request carries the token, and the server just validates it.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ─── Disable CSRF for REST APIs ────────────────────────────────────
            // CSRF protection is for browser-based session cookies.
            // Since we use JWT (stateless), CSRF is not needed.
            .csrf(AbstractHttpConfigurer::disable)

            // ─── Session Management: STATELESS ─────────────────────────────────
            // Don't create HTTP sessions — every request is independent
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // ─── URL Authorization Rules ────────────────────────────────────────
            .authorizeHttpRequests(auth -> auth
                // Public endpoints — no token required
                .requestMatchers(
                    "/api/v1/auth/**",    // registration + OTP endpoints
                    "/actuator/**",       // health checks
                    "/error"              // Spring error page
                ).permitAll()
                // All other endpoints require authentication
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
