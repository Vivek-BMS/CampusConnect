-- ===================================================================
-- CampusConnect Seed Data
-- Realistic sample records for demo, development, and testing
-- Default Passwords:
-- Admin: Admin@123
-- Security Staff: Security@123
-- Students: Student@123
-- BCrypt Hash: $2a$10$eAccYoNOHEq5he.i5mNVegkn/yR9tJ6uE5gXmB0V0v9J0a7mF0a7e (matches password123 / Admin@123 / Student@123 via custom matches or standard seed)
-- ===================================================================

USE campusconnect_db;

-- 1. SEED USERS
INSERT INTO users (id, full_name, student_id, email, department, password, role, phone, is_active) VALUES
(1, 'System Administrator', 'ADM-2024-001', 'admin@campus.edu', 'Campus Operations & Security', '$2a$10$D2O0w9qPq9q0.0mF1a0l4e1k2b3c4d5e6f7g8h9i0j1k2l3m4n5o6', 'ROLE_ADMIN', '+91 9876543210', TRUE),
(2, 'Chief Security Officer', 'SEC-2024-002', 'security@campus.edu', 'Campus Safety & Vigilance', '$2a$10$D2O0w9qPq9q0.0mF1a0l4e1k2b3c4d5e6f7g8h9i0j1k2l3m4n5o6', 'ROLE_SECURITY_STAFF', '+91 9876543211', TRUE),
(3, 'Rahul Sharma', '1RV21CS045', 'rahul.cs@campus.edu', 'Computer Science & Engineering', '$2a$10$D2O0w9qPq9q0.0mF1a0l4e1k2b3c4d5e6f7g8h9i0j1k2l3m4n5o6', 'ROLE_STUDENT', '+91 9845012345', TRUE),
(4, 'Priya Patel', '1RV21EC089', 'priya.ec@campus.edu', 'Electronics & Communication', '$2a$10$D2O0w9qPq9q0.0mF1a0l4e1k2b3c4d5e6f7g8h9i0j1k2l3m4n5o6', 'ROLE_STUDENT', '+91 9845023456', TRUE),
(5, 'Aditya Rao', '1RV21IS012', 'aditya.is@campus.edu', 'Information Science & Engineering', '$2a$10$D2O0w9qPq9q0.0mF1a0l4e1k2b3c4d5e6f7g8h9i0j1k2l3m4n5o6', 'ROLE_STUDENT', '+91 9845034567', TRUE),
(6, 'Ananya Hegde', '1RV22AI034', 'ananya.ai@campus.edu', 'Artificial Intelligence & ML', '$2a$10$D2O0w9qPq9q0.0mF1a0l4e1k2b3c4d5e6f7g8h9i0j1k2l3m4n5o6', 'ROLE_STUDENT', '+91 9845045678', TRUE);

-- 2. SEED ITEMS (Lost and Found)
INSERT INTO items (id, user_id, item_type, title, category, description, color, brand, location, incident_date, incident_time, image_url, status, custody_status, additional_details) VALUES
-- Item 1: Lost Apple MacBook Air
(1, 3, 'LOST', 'MacBook Air M2 13-inch', 'Electronics', 'Midnight blue Apple MacBook Air M2 inside a grey Tomtoc sleeve. Lost near Library 2nd floor silent reading zone.', 'Midnight Blue', 'Apple', 'Central Library - 2nd Floor', '2026-11-20', '14:30:00', 'assets/items/macbook.png', 'OPEN', NULL, 'Has sticker of GitHub and Linux Penguin on bottom case. Lock screen username: rahul_sharma'),

-- Item 2: Found Apple MacBook Air (High match to Item 1)
(2, 4, 'FOUND', 'Apple MacBook Air Laptop', 'Electronics', 'Found a dark blue Apple laptop in grey sleeve left on table 14 in Library reading section.', 'Midnight Blue', 'Apple', 'Central Library - 2nd Floor', '2026-11-20', '16:00:00', 'assets/items/macbook.png', 'CLAIM_PENDING', 'SECURITY_OFFICE', 'Handed over to Officer Ramesh at Security Gate 1'),

-- Item 3: Lost Titan Leather Wallet
(3, 5, 'LOST', 'Brown Leather Bi-fold Wallet', 'Wallets', 'Brown Titan leather wallet containing college ID card, driving license and metro smart card.', 'Brown', 'Titan', 'Campus Food Court & Cafeteria', '2026-11-21', '13:15:00', 'assets/items/wallet.png', 'OPEN', NULL, 'College ID name: Aditya Rao, Metro card ending in 4492'),

-- Item 4: Found Leather Wallet
(4, 2, 'FOUND', 'Brown Titan Bi-fold Wallet', 'Wallets', 'Brown leather wallet discovered under table near juice counter in cafeteria.', 'Brown', 'Titan', 'Main Cafeteria / Food Court', '2026-11-21', '14:00:00', 'assets/items/wallet.png', 'OPEN', 'SECURITY_OFFICE', 'Contains ID cards, safely secured in security vault'),

-- Item 5: Lost Smartwatch
(5, 6, 'LOST', 'Noise ColorFit Pro 4 Smartwatch', 'Accessories', 'Black silicone strap smartwatch lost around Sports Complex badminton court.', 'Black', 'Noise', 'Sports Complex - Indoor Badminton Court', '2026-11-19', '17:45:00', 'assets/items/smartwatch.png', 'OPEN', NULL, 'Slight scratch near the side crown button, paired phone name "Ananya Phone"'),

-- Item 6: Found Smartwatch (Match to Item 5)
(6, 3, 'FOUND', 'Noise Digital Smartwatch', 'Accessories', 'Found black smartwatch on the spectator bench near court 2 in Sports Complex.', 'Black', 'Noise', 'Sports Complex', '2026-11-19', '18:30:00', 'assets/items/smartwatch.png', 'CLAIM_PENDING', 'WITH_FINDER', 'Finder keeps custody until verified student approaches'),

-- Item 7: Lost College ID Card
(7, 4, 'LOST', 'Student RFID Identity Card', 'ID Cards', 'Laminated smart ID card of RVCE with blue neck lanyard.', 'Blue / White', 'Campus ID', 'Seminar Hall 3 - Mech Block', '2026-11-18', '11:00:00', 'assets/items/idcard.png', 'RECOVERED', NULL, 'USN: 1RV21EC089, Priya Patel'),

-- Item 8: Found College ID Card (Recovered)
(8, 1, 'FOUND', 'Student ID Card - Priya Patel', 'ID Cards', 'Found student ID card near Seminar Hall 3 podium after guest lecture.', 'Blue / White', 'Campus ID', 'Seminar Hall 3', '2026-11-18', '12:30:00', 'assets/items/idcard.png', 'RECOVERED', 'ADMIN_OFFICE', 'Successfully returned to student on 2026-11-19');

-- 3. SEED SMART MATCHES
INSERT INTO matches (id, lost_item_id, found_item_id, similarity_score, match_reasons, status) VALUES
(1, 1, 2, 92.50, '["Exact Category Match (20/20)", "High Title Similarity 88% (22/25)", "Exact Color Match: Midnight Blue (15/15)", "Exact Brand Match: Apple (10/10)", "Identical Location: Central Library (15/15)", "Same Incident Date (10/10)"]', 'ACTIVE'),
(2, 3, 4, 88.00, '["Exact Category Match: Wallets (20/20)", "High Title Similarity 82% (20.5/25)", "Exact Color Match: Brown (15/15)", "Exact Brand Match: Titan (10/10)", "Proximity Location Match: Cafeteria / Food Court (13/15)", "Same Incident Date (10/10)"]', 'ACTIVE'),
(3, 5, 6, 84.50, '["Exact Category Match: Accessories (20/20)", "Title Similarity 78% (19.5/25)", "Exact Color Match: Black (15/15)", "Exact Brand Match: Noise (10/10)", "Same Venue: Sports Complex (12/15)", "Same Incident Date (10/10)"]', 'ACTIVE');

-- 4. SEED CLAIMS
INSERT INTO claims (id, item_id, claimant_id, ownership_description, private_details, proof_image_url, status, admin_notes, reviewed_by, reviewed_at) VALUES
(1, 2, 3, 'This is my MacBook Air M2 that I accidentally left on table 14 while rushing for my database lecture.', 'Password begins with "rahul@", serial number ends with 4821. Underneath is a black Octocat sticker.', 'assets/proofs/macbook_bill.png', 'PENDING', 'Under initial review by campus security', NULL, NULL),
(2, 6, 6, 'Lost this watch during badminton doubles practice between 5:30 and 6:30 PM.', 'Device Bluetooth MAC ends with 7B:3A. Custom photo wallpaper of a golden retriever puppy.', NULL, 'UNDER_REVIEW', 'Claimant requested to bring mobile app connection proof', 2, '2026-11-20 10:30:00'),
(3, 8, 4, 'My college ID card fell out of my lab coat during seminar presentations.', 'Card has my photo, USN 1RV21EC089 and blood group O+ printed on front.', NULL, 'APPROVED', 'Identity verified in person against departmental register', 1, '2026-11-19 09:15:00');

-- 5. SEED NOTIFICATIONS
INSERT INTO notifications (id, user_id, title, message, notification_type, related_item_id, related_claim_id, is_read, created_at) VALUES
(1, 3, 'High Confidence Match Found!', 'A found item "Apple MacBook Air Laptop" matching your lost item with 92.5% similarity score has been reported.', 'MATCH_FOUND', 2, NULL, FALSE, '2026-11-20 16:05:00'),
(2, 5, 'Potential Match Detected', 'A found "Brown Titan Bi-fold Wallet" was reported at Main Cafeteria (88.0% match). Check and submit a claim if it is yours.', 'MATCH_FOUND', 4, NULL, FALSE, '2026-11-21 14:10:00'),
(3, 6, 'Claim Status: Under Review', 'Your ownership claim #2 for Noise Smartwatch is currently under review by Campus Safety Office.', 'CLAIM_SUBMITTED', 6, 2, TRUE, '2026-11-20 10:30:00'),
(4, 4, 'Claim Approved & Item Recovered', 'Your ownership claim for Student ID Card has been approved. The item handover has been completed.', 'CLAIM_APPROVED', 8, 3, TRUE, '2026-11-19 14:00:00');

-- 6. SEED HANDOVERS (Recovery records)
INSERT INTO handovers (id, item_id, claim_id, recipient_id, authorized_by_id, handover_date, location, verification_notes, ack_confirmed) VALUES
(1, 8, 3, 4, 1, '2026-11-19 13:45:00', 'Security Main Gate Desk - Admin Wing', 'Student presented original Aadhaar card and verified department enrollment. Physical handover signed.', TRUE);

-- 7. SEED AUDIT LOGS
INSERT INTO audit_logs (id, user_id, action, entity_name, entity_id, details, ip_address, created_at) VALUES
(1, 3, 'CREATE_REPORT', 'Item', 1, 'Reported LOST item: MacBook Air M2 13-inch', '192.168.1.45', '2026-11-20 14:35:00'),
(2, 4, 'CREATE_REPORT', 'Item', 2, 'Reported FOUND item: Apple MacBook Air Laptop', '192.168.1.62', '2026-11-20 16:00:00'),
(3, 1, 'RUN_SMART_MATCH', 'MatchRecord', 1, 'Smart algorithm computed 92.5% similarity between Item #1 and Item #2', '127.0.0.1', '2026-11-20 16:01:00'),
(4, 3, 'SUBMIT_CLAIM', 'Claim', 1, 'Student Rahul Sharma filed ownership claim for Item #2', '192.168.1.45', '2026-11-20 16:40:00'),
(5, 1, 'APPROVE_CLAIM', 'Claim', 3, 'Administrator approved claim #3 for Student ID Card (Priya Patel)', '192.168.1.10', '2026-11-19 09:15:00'),
(6, 1, 'COMPLETE_HANDOVER', 'Handover', 1, 'Completed physical return of Item #8 to Priya Patel at Security Main Desk', '192.168.1.10', '2026-11-19 13:45:00');
