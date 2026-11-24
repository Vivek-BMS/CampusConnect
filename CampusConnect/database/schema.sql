-- ===================================================================
-- CampusConnect Database Schema
-- Intelligent Campus Lost & Found Management System
-- DBMS: MySQL 8.0+
-- ===================================================================

CREATE DATABASE IF NOT EXISTS campusconnect_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE campusconnect_db;

-- Drop tables in reverse order of dependencies if recreating
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS handovers;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS matches;
DROP TABLE IF EXISTS claims;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. USERS TABLE
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    student_id VARCHAR(50) NOT NULL UNIQUE COMMENT 'USN or Employee ID',
    email VARCHAR(120) NOT NULL UNIQUE COMMENT 'Institutional email address',
    department VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL COMMENT 'BCrypt hashed password',
    role VARCHAR(30) NOT NULL DEFAULT 'ROLE_STUDENT' COMMENT 'ROLE_STUDENT, ROLE_ADMIN, ROLE_SECURITY_STAFF',
    phone VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_student_id (student_id),
    INDEX idx_user_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. ITEMS TABLE
CREATE TABLE items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    item_type VARCHAR(20) NOT NULL COMMENT 'LOST, FOUND',
    title VARCHAR(150) NOT NULL,
    category VARCHAR(50) NOT NULL COMMENT 'Electronics, ID Cards, Books, Bags, Wallets, Accessories, Clothing, Other',
    description TEXT NOT NULL,
    color VARCHAR(50),
    brand VARCHAR(80),
    location VARCHAR(150) NOT NULL,
    incident_date DATE NOT NULL,
    incident_time TIME,
    image_url VARCHAR(255),
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN' COMMENT 'OPEN, CLAIM_PENDING, CLAIM_APPROVED, RECOVERED, CLOSED',
    custody_status VARCHAR(40) COMMENT 'WITH_FINDER, SECURITY_OFFICE, ADMIN_OFFICE',
    additional_details TEXT COMMENT 'Private identifying marks known only to owner or finder',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_items_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_items_type (item_type),
    INDEX idx_items_category (category),
    INDEX idx_items_status (status),
    INDEX idx_items_date (incident_date),
    INDEX idx_items_location (location)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. CLAIMS TABLE
CREATE TABLE claims (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    claimant_id BIGINT NOT NULL,
    ownership_description TEXT NOT NULL,
    private_details TEXT NOT NULL COMMENT 'Secret identifying details or serial/IMEI/PIN hint',
    proof_image_url VARCHAR(255),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, UNDER_REVIEW, APPROVED, REJECTED',
    admin_notes TEXT,
    reviewed_by BIGINT,
    reviewed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_claims_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    CONSTRAINT fk_claims_claimant FOREIGN KEY (claimant_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_claims_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_claims_status (status),
    INDEX idx_claims_item (item_id),
    INDEX idx_claims_claimant (claimant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. MATCHES TABLE
CREATE TABLE matches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lost_item_id BIGINT NOT NULL,
    found_item_id BIGINT NOT NULL,
    similarity_score DECIMAL(5,2) NOT NULL COMMENT 'Weighted similarity percentage (0.00 - 100.00)',
    match_reasons TEXT NOT NULL COMMENT 'JSON or text list of scoring factors',
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, DISMISSED, CONFIRMED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_matches_lost FOREIGN KEY (lost_item_id) REFERENCES items(id) ON DELETE CASCADE,
    CONSTRAINT fk_matches_found FOREIGN KEY (found_item_id) REFERENCES items(id) ON DELETE CASCADE,
    CONSTRAINT uk_lost_found UNIQUE (lost_item_id, found_item_id),
    INDEX idx_matches_score (similarity_score),
    INDEX idx_matches_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. NOTIFICATIONS TABLE
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL COMMENT 'MATCH_FOUND, CLAIM_SUBMITTED, CLAIM_APPROVED, CLAIM_REJECTED, ITEM_RECOVERED, SYSTEM',
    related_item_id BIGINT,
    related_claim_id BIGINT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notifications_user (user_id),
    INDEX idx_notifications_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. HANDOVERS TABLE
CREATE TABLE handovers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    claim_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    authorized_by_id BIGINT NOT NULL,
    handover_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    location VARCHAR(150) NOT NULL,
    verification_notes TEXT,
    ack_confirmed BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_handovers_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    CONSTRAINT fk_handovers_claim FOREIGN KEY (claim_id) REFERENCES claims(id) ON DELETE CASCADE,
    CONSTRAINT fk_handovers_recipient FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_handovers_authorizer FOREIGN KEY (authorized_by_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_handovers_item (item_id),
    INDEX idx_handovers_recipient (recipient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. AUDIT LOGS TABLE
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    entity_name VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    details TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_audit_action (action),
    INDEX idx_audit_entity (entity_name, entity_id),
    INDEX idx_audit_timestamp (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
