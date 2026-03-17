-- ============================================================
-- FMS Auth Database Schema
-- ============================================================
-- Run this script manually OR let Spring Boot auto-create
-- tables via: spring.jpa.hibernate.ddl-auto=update


-- ============================================================
-- Table: fms_user_master
-- Purpose: Stores all registered users
-- ============================================================
use webapp;
DROP TABLE IF EXISTS `fms_user_master`;

CREATE TABLE
  `fms_user_master` (
    `id` int NOT NULL AUTO_INCREMENT,
    `user_id` int NOT NULL,
    `role_id` varchar(25) DEFAULT NULL,
    `establishment_id` int DEFAULT NULL,
    `user_name` varchar(95) DEFAULT NULL,
    `email` varchar(45) DEFAULT NULL,
    `password` varchar(255) DEFAULT NULL,
    `company_name` varchar(255) DEFAULT NULL,
    `designation` varchar(255) DEFAULT NULL,
    `session_token` varchar(255) DEFAULT NULL,
    `mobile_number` bigint DEFAULT NULL,
    `designation_id` int DEFAULT NULL,
    `role_name` varchar(45) DEFAULT NULL,
    `user_image` varchar(45) DEFAULT NULL,
    `status` tinyint DEFAULT NULL,
    `created_by` varchar(45) DEFAULT NULL,
    `created_date` datetime DEFAULT NULL,
    `modified_date` datetime DEFAULT NULL,
    `modified_by` varchar(45) DEFAULT NULL,
    `modified_password_by` varchar(255) DEFAULT NULL,
    `modified_password_date` datetime DEFAULT NULL,
    PRIMARY KEY (`id`, `user_id`),
    UNIQUE KEY `email_UNIQUE` (`email`),
    KEY `idx_id` (`id`),
    KEY `idx_userid` (`user_id`)
  ) ENGINE = InnoDB AUTO_INCREMENT = 192 DEFAULT CHARSET = latin1;

-- ============================================================
-- Table: fms_mobile_otp
-- Purpose: Stores OTP records
-- ============================================================
CREATE TABLE IF NOT EXISTS fms_mobile_otp (
    otp_id          BIGINT          NOT NULL AUTO_INCREMENT,
    mobile_number   BIGINT          NOT NULL COMMENT 'Mobile number the OTP was sent to',
    otp_code        VARCHAR(10)     NOT NULL COMMENT '6-digit OTP code',
    expires_at      DATETIME        NOT NULL COMMENT 'OTP expiry time (created_at + 5 min)',
    is_used         BOOLEAN         NOT NULL DEFAULT FALSE COMMENT 'True if OTP was consumed/invalidated',
    is_verified     BOOLEAN         NOT NULL DEFAULT FALSE COMMENT 'True if OTP was verified successfully',
    attempt_count   INT             NOT NULL DEFAULT 0 COMMENT 'Number of verification attempts (max 3)',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (otp_id),
    INDEX idx_otp_mobile (mobile_number),
    INDEX idx_otp_active (mobile_number, is_used, is_verified)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='FMS Mobile OTP - stores OTPs for verification';



-- ============================================================
-- Sample Data (for testing only)
-- ============================================================
-- INSERT INTO fms_user_master (username, email, mobile_number)
-- VALUES ('Test User', 'test@example.com', '9876543210');
