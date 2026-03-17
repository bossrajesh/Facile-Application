# FMS Auth — User Registration & Mobile OTP Verification

A Spring Boot application providing user registration and mobile OTP-based login
with JWT token authentication, backed by MySQL.

---

## 📁 Folder Structure

```
fms-auth/
├── pom.xml                                          ← Maven dependencies
└── src/main/
    ├── java/com/fms/auth/
    │   ├── FmsAuthApplication.java                  ← App entry point
    │   │
    │   ├── config/
    │   │   └── SecurityConfig.java                  ← Spring Security rules
    │   │
    │   ├── controller/
    │   │   ├── UserRegistrationController.java      ← POST /register
    │   │   └── OtpController.java                   ← POST /otp/send & /otp/verify
    │   │
    │   ├── dto/
    │   │   ├── request/
    │   │   │   ├── UserRegistrationRequest.java     ← Registration form
    │   │   │   ├── OtpSendRequest.java              ← OTP send form
    │   │   │   └── OtpVerifyRequest.java            ← OTP verify form
    │   │   └── response/
    │   │       ├── ApiResponse.java                 ← Standard response wrapper
    │   │       ├── UserRegistrationResponse.java    ← Registration result
    │   │       ├── OtpSendResponse.java             ← OTP send result
    │   │       └── OtpVerifyResponse.java           ← JWT token result
    │   │
    │   ├── entity/
    │   │   ├── UserMaster.java                      ← fms_user_master table
    │   │   └── MobileOtp.java                       ← fms_mobile_otp table
    │   │
    │   ├── exception/
    │   │   ├── DuplicateResourceException.java      ← 409 Conflict
    │   │   ├── ResourceNotFoundException.java       ← 404 Not Found
    │   │   ├── InvalidOtpException.java             ← 400 Bad Request
    │   │   ├── OtpMaxAttemptsException.java         ← 429 Too Many Requests
    │   │   └── GlobalExceptionHandler.java          ← Catches all exceptions
    │   │
    │   ├── repository/
    │   │   ├── UserMasterRepository.java            ← DB queries for users
    │   │   └── MobileOtpRepository.java             ← DB queries for OTPs
    │   │
    │   ├── service/
    │   │   ├── UserService.java                     ← Interface
    │   │   ├── OtpService.java                      ← Interface
    │   │   └── impl/
    │   │       ├── UserServiceImpl.java             ← Registration logic
    │   │       └── OtpServiceImpl.java              ← OTP send/verify logic
    │   │
    │   └── util/
    │       ├── JwtUtil.java                         ← JWT generate/validate
    │       └── OtpUtil.java                         ← OTP code generator
    │
    └── resources/
        ├── application.properties                   ← App configuration
        └── schema.sql                               ← MySQL table definitions
```



## 🌐 API Endpoints

### 1. Register User
```
POST /api/v1/auth/register
```
**Request:**
```json
{
  "userName": "John Doe",
  "designation": "Senior Manager",
  "companyName": "Acme Corporation",
  "mobileNumber": "9876543210",
  "email": "rajesh.kumar2@example.com",
  "userId": 66
}
```
**Success → 201 Created:**
```json
{
  "success": true,
  "httpStatus": 201,
  "message": "User registered successfully",
  "data": {
    "userId": 1,
    "username": "John Doe",
    "email": "john@example.com",
    "mobileNumber": "9876543210",
    "isMobileVerified": false,
    "status": "ACTIVE",
    "message": "User registered successfully. Please verify your mobile number via OTP."
  },
  "timestamp": "2024-01-15T10:30:00"
}
```
**Error → 409 Conflict (mobile exists):**
```json
{
  "success": false,
  "httpStatus": 409,
  "message": "Mobile Number already exists",
  "timestamp": "2024-01-15T10:30:00"
}
```

---

### 2. Send OTP
```
POST /api/v1/auth/otp/send
```
**Request:**
```json
{
  "mobileNumber": "9876543210"
}
```
**Success → 200 OK:**
```json
{
  "success": true,
  "httpStatus": 200,
  "message": "OTP sent successfully to registered mobile number",
  "data": {
    "mobileNumber": "9876543210",
    "otpCode": "482910",
    "expiresIn": "OTP is valid for 5 minutes",
    "message": "OTP sent successfully to your registered mobile number."
  }
}
```
**Error → 404 Not Found:**
```json
{
  "success": false,
  "httpStatus": 404,
  "message": "Mobile number does not exist. Please register first."
}
```

---

### 3. Verify OTP
```
POST /api/v1/auth/otp/verify
```
**Request:**
```json
{
  "mobileNumber": "9876543210",
  "otpCode": "482910"
}
```
**Success → 200 OK:**
```json
{
  "success": true,
  "httpStatus": 200,
  "message": "OTP verified successfully. You are now logged in.",
  "data": {
    "mobileNumber": "9876543210",
    "username": "John Doe",
    "jwtToken": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "message": "OTP verified successfully. You are now logged in."
  }
}
```
**Error → 400 (wrong OTP):**
```json
{
  "success": false,
  "httpStatus": 400,
  "message": "Invalid OTP. You have 2 attempt(s) remaining."
}
```
**Error → 429 (too many attempts):**
```json
{
  "success": false,
  "httpStatus": 429,
  "message": "Maximum OTP attempts exceeded. Please request a new OTP."
}
```

---

## 🔒 HTTP Status Codes Reference

| Status | Code | When It's Returned                            |
|--------|------|-----------------------------------------------|
| Created | 201 | User registered successfully                  |
| OK | 200 | OTP sent / OTP verified with JWT              |
| Bad Request | 400 | Validation failed / OTP invalid or expired    |
| Not Found | 404 | Mobile number not registered                  |
| Conflict | 409 | Mobile number or email already exists         |
| Too Many Requests | 429 | OTP attempts exceeded (> 3)                   |
| Internal Server Error | 500 | Unexpected server error                       |

---

## 🚀 How to Run

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+

### Setup

1. Run the application:
   ```bash
   cd fms-auth
   mvn spring-boot:run
   ```

### Test with Postman

**Step 1:** Register
```
POST http://localhost:8080/api/v1/auth/register
```

**Step 2:** Send OTP
```
POST http://localhost:8080/api/v1/auth/otp/send
```

**Step 3:** Verify OTP → Get JWT Token
```
POST http://localhost:8080/api/v1/auth/otp/verify
```

**Step 4:** Use JWT for protected APIs
```
GET http://localhost:8080/api/v1/protected-resource
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## 🔐 Security Notes

- JWT secret should be a strong Base64-encoded 256-bit key in production
- OTP expires after 5 minutes and is locked after 3 failed attempts
- Passwords are masked in logs
