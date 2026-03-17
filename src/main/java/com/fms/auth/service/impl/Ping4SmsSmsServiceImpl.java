package com.fms.auth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fms.auth.dto.response.SmsResponse;
import com.fms.auth.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Ping4SMS SMS Service Implementation
 * -------------------------------------
 * Sends OTP SMS via the Ping4SMS HTTP GET API.
 *
 * API endpoint: https://www.pingsms.in/api/sendsms
 *
 * Required query parameters:
 *   key        → your API key (X-Authorization header too)
 *   sender     → your approved Sender ID (e.g. FMSAPP)
 *   mobile     → 10-digit mobile number
 *   language   → 1 (English) or 2 (Unicode)
 *   product    → 1 (Transactional/OTP) or 2 (Promotional)
 *   message    → URL-encoded SMS text
 *   template   → DLT approved Template ID (mandatory for India)
 *
 * How it works (plain English):
 *   1. Build the URL with all parameters
 *   2. Add X-Authorization header with your API key
 *   3. Make a GET request to Ping4SMS server
 *   4. Ping4SMS delivers the SMS to the user's phone
 *   5. We log the delivery ID for tracking
 *
 * DLT (Distributed Ledger Technology) Note:
 *   As per TRAI regulations in India, all transactional SMS
 *   must be registered with a DLT platform. Your message content
 *   must exactly match the approved template, with only the OTP
 *   value changing. The template-id links to your approved template.
 */
@Service
@RequiredArgsConstructor
public class Ping4SmsSmsServiceImpl implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(Ping4SmsSmsServiceImpl.class);

    private final ObjectMapper objectMapper;

    @Value("${app.sms.ping4sms.api-url}")
    private String apiUrl;

    @Value("${app.sms.ping4sms.api-key}")
    private String apiKey;

    @Value("${app.sms.ping4sms.sender-id}")
    private String senderId;

    @Value("${app.sms.ping4sms.template-id}")
    private String templateId;

    @Value("${app.sms.ping4sms.otp-message}")
    private String otpMessageTemplate;

    @Value("${app.sms.ping4sms.enabled:true}")
    private boolean smsEnabled;

    // HTTP client with 10-second timeout
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Sends OTP SMS via Ping4SMS API.
     *
     * @param mobileNumber  10-digit mobile number
     * @param otpCode       6-digit OTP
     */
    @Override
    public void sendOtpSms(String mobileNumber, String otpCode) {

        // ─── Dev mode: skip actual SMS, just log ──────────────────────────
        if (!smsEnabled) {
            log.info("[SMS DISABLED] OTP {} for mobile {} (Ping4SMS call skipped)",
                    otpCode, maskMobile(mobileNumber));
            return;
        }

        try {
            // ─── Step 1: Build the SMS message text ───────────────────────
            // Replace {otp} placeholder with the actual OTP
            String messageText = otpMessageTemplate.replace("{otp}", otpCode);

            // URL-encode the message so special characters don't break the URL
            String encodedMessage = URLEncoder.encode(messageText, StandardCharsets.UTF_8);

            String requestUrl = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .queryParam("key",      apiKey)
                    .queryParam("sender",   senderId)
                    .queryParam("number",   mobileNumber)
                    .queryParam("route",  4)
                    .queryParam("sms",  encodedMessage)
                    .queryParam("templateid", templateId)
                    .build(true)  // true = do not encode again (already encoded message)
                    .toUriString();

            // ─── Step 3: Build the HTTP GET request ───────────────────────
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("X-Authorization", apiKey)   // required auth header
                    .header("Accept", "application/json")
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();

            log.info("Sending OTP SMS via Ping4SMS to mobile: {}", maskMobile(mobileNumber));

            // ─── Step 4: Execute the HTTP call ────────────────────────────
            HttpResponse<String> httpResponse = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            // ─── Step 5: Parse and validate the response ──────────────────
            handleResponse(httpResponse, mobileNumber);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("SMS send interrupted for mobile: {}", maskMobile(mobileNumber), e);
            throw new RuntimeException("SMS sending was interrupted. Please try again.");

        } catch (Exception e) {
            log.error("Failed to send SMS to mobile: {} | Error: {}",
                    maskMobile(mobileNumber), e.getMessage(), e);
            throw new RuntimeException("Failed to send OTP SMS. Please try again later.");
        }
    }

    /**
     * Parse the Ping4SMS response and handle success/failure.
     *
     * Success response: { "message": "SMS Submitted Successfully", "status": "success", "id": 12345 }
     * Error response:   { "message": "Invalid API Key", "status": "error" }
     */
    private void handleResponse(HttpResponse<String> httpResponse, String mobileNumber) {
        String responseBody = httpResponse.body();
        int httpStatus = httpResponse.statusCode();

        log.debug("Ping4SMS HTTP status: {} | Response: {}", httpStatus, responseBody);

        if (httpStatus != 200) {
            log.error("Ping4SMS returned HTTP {} for mobile: {}", httpStatus, maskMobile(mobileNumber));
            throw new RuntimeException(
                    "SMS gateway returned error status: " + httpStatus + ". Please try again.");
        }

        try {
            SmsResponse smsResponse = objectMapper.readValue(responseBody, SmsResponse.class);

            if (smsResponse.isSuccess()) {
                log.info("OTP SMS sent successfully to: {} | Delivery ID: {}",
                        maskMobile(mobileNumber), smsResponse.getId());
            } else {
                log.error("Ping4SMS rejected SMS for mobile: {} | Reason: {}",
                        maskMobile(mobileNumber), smsResponse.getMessage());
                throw new RuntimeException(
                        "SMS could not be delivered: " + smsResponse.getMessage());
            }

        } catch (RuntimeException e) {
            throw e; // re-throw our own exceptions
        } catch (Exception e) {
            // Response wasn't JSON — log as warning but don't block the flow
            log.warn("Could not parse Ping4SMS response as JSON: {} | Raw: {}",
                    e.getMessage(), responseBody);
        }
    }

    private String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 4) return "****";
        return "******" + mobile.substring(mobile.length() - 4);
    }
}
