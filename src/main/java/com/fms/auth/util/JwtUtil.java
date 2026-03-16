package com.fms.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Utility
 * -----------
 * JWT = JSON Web Token
 *
 * Think of a JWT like a "digital ID card" issued after successful OTP verification.
 * The user carries this card and shows it every time they access a protected resource.
 *
 * A JWT has 3 parts (separated by dots):
 *   HEADER.PAYLOAD.SIGNATURE
 *
 *   - Header: says what algorithm was used
 *   - Payload: contains user info (mobile number, expiry time, etc.)
 *   - Signature: proves the token was issued by us and not tampered with
 *
 * The server NEVER stores the token — it just validates the signature.
 */
@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private Long jwtExpiration; // in milliseconds

    /**
     * Get the signing key from our secret.
     * This key is used to "sign" tokens we issue and "verify" tokens we receive.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate a JWT token for a verified user.
     *
     * @param mobileNumber - the unique identifier for the user
     * @param username     - user's name (added as extra info in the token)
     * @return signed JWT token string
     */
    public String generateToken(String mobileNumber, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("mobileNumber", mobileNumber);
        claims.put("tokenType", "OTP_VERIFIED");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(mobileNumber)               // who the token is for
                .setIssuedAt(new Date())                // when it was issued
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // when it expires
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)                 // sign it
                .compact();
    }

    /**
     * Extract the mobile number (subject) from a token.
     */
    public String getMobileNumberFromToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extract the expiration date from a token.
     */
    public Date getExpirationFromToken(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /**
     * Get token expiry duration in seconds (for response).
     */
    public Long getExpirationInSeconds() {
        return jwtExpiration / 1000;
    }

    /**
     * Check if a token is valid.
     * A token is valid if:
     *   1. It was signed with our secret key
     *   2. It hasn't expired
     *   3. The mobile number in the token matches the one provided
     */
    public boolean validateToken(String token, String mobileNumber) {
        try {
            String tokenMobile = getMobileNumberFromToken(token);
            return tokenMobile.equals(mobileNumber) && !isTokenExpired(token);
        } catch (Exception e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if token has passed its expiry time.
     */
    private boolean isTokenExpired(String token) {
        return getExpirationFromToken(token).before(new Date());
    }

    /**
     * Parse and extract all claims from the token.
     * Throws exception if token is invalid or tampered.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Validate token and return meaningful error messages.
     */
    public boolean validateTokenWithLogging(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
