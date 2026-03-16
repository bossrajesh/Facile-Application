-- ============================================================
-- FMS Auth Database Schema
-- ============================================================
-- Run this script manually OR let Spring Boot auto-create
-- tables via: spring.jpa.hibernate.ddl-auto=update
-- ============================================================

-- Create database (if not already created)
CREATE DATABASE IF NOT EXISTS fms_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE fms_db;

-- ============================================================
-- Table: fms_user_master
-- Purpose: Stores all registered users
-- ============================================================
CREATE TABLE IF NOT EXISTS fms_user_master (
    user_id          BIGINT          NOT NULL AUTO_INCREMENT,
    username         VARCHAR(100)    NOT NULL COMMENT 'Full name of the user',
    email            VARCHAR(150)    NOT NULL COMMENT 'Email address - must be unique',
    mobile_number    VARCHAR(15)     NOT NULL COMMENT 'Mobile number - must be unique, used for OTP',
    is_mobile_verified BOOLEAN       NOT NULL DEFAULT FALSE COMMENT 'True after OTP verification',
    status           VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE or INACTIVE',
    created_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id),
    UNIQUE KEY uk_mobile_number (mobile_number),
    UNIQUE KEY uk_email (email),
    INDEX idx_mobile_number (mobile_number),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='FMS User Master - stores registered users';


-- ============================================================
-- Table: fms_mobile_otp
-- Purpose: Stores OTPs sent for mobile verification/login
-- ============================================================
CREATE TABLE IF NOT EXISTS fms_mobile_otp (
    otp_id          BIGINT          NOT NULL AUTO_INCREMENT,
    mobile_number   VARCHAR(15)     NOT NULL COMMENT 'Mobile number the OTP was sent to',
    otp_code        VARCHAR(10)     NOT NULL COMMENT '6-digit OTP code',
    expires_at      DATETIME        NOT NULL COMMENT 'OTP expiry time (created_at + 5 min)',
    is_used         BOOLEAN         NOT NULL DEFAULT FALSE COMMENT 'True if OTP was consumed/invalidated',
    is_verified     BOOLEAN         NOT NULL DEFAULT FALSE COMMENT 'True if OTP was verified successfully',
    attempt_count   INT             NOT NULL DEFAULT 0 COMMENT 'Number of verification attempts (max 3)',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (otp_id),
    INDEX idx_otp_mobile (mobile_number),
    INDEX idx_otp_active (mobile_number, is_used, is_verified),
    FOREIGN KEY fk_otp_mobile (mobile_number)
        REFERENCES fms_user_master(mobile_number)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='FMS Mobile OTP - stores OTPs for verification';


-- ============================================================
-- Sample Data (for testing only)
-- ============================================================
-- INSERT INTO fms_user_master (username, email, mobile_number)
-- VALUES ('Test User', 'test@example.com', '9876543210');
