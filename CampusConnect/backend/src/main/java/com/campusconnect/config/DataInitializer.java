package com.campusconnect.config;

import com.campusconnect.entity.*;
import com.campusconnect.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MatchRecordRepository matchRecordRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private HandoverRepository handoverRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (userRepository.count() > 0) {
                logger.info("Database already contains data, skipping initialization.");
                return;
            }

            logger.info("Initializing demo seed data for CampusConnect...");

            // 1. Users
            User admin = new User("System Administrator", "ADM-2024-001", "admin@campus.edu",
                    "Campus Operations & Security", passwordEncoder.encode("Admin@123"), Role.ROLE_ADMIN);
            admin.setPhone("+91 9876543210");
            userRepository.save(admin);

            User security = new User("Chief Security Officer", "SEC-2024-002", "security@campus.edu",
                    "Campus Safety & Vigilance", passwordEncoder.encode("Security@123"), Role.ROLE_SECURITY_STAFF);
            security.setPhone("+91 9876543211");
            userRepository.save(security);

            User student1 = new User("Rahul Sharma", "1RV21CS045", "rahul.cs@campus.edu",
                    "Computer Science & Engineering", passwordEncoder.encode("Student@123"), Role.ROLE_STUDENT);
            student1.setPhone("+91 9845012345");
            userRepository.save(student1);

            User student2 = new User("Priya Patel", "1RV21EC089", "priya.ec@campus.edu",
                    "Electronics & Communication", passwordEncoder.encode("Student@123"), Role.ROLE_STUDENT);
            student2.setPhone("+91 9845023456");
            userRepository.save(student2);

            User student3 = new User("Aditya Rao", "1RV21IS012", "aditya.is@campus.edu",
                    "Information Science & Engineering", passwordEncoder.encode("Student@123"), Role.ROLE_STUDENT);
            student3.setPhone("+91 9845034567");
            userRepository.save(student3);

            // 2. Items
            Item item1 = new Item();
            item1.setReporter(student1);
            item1.setItemType(ItemType.LOST);
            item1.setTitle("MacBook Air M2 13-inch");
            item1.setCategory("Electronics");
            item1.setDescription("Midnight blue Apple MacBook Air M2 in grey Tomtoc sleeve. Lost near Library 2nd floor.");
            item1.setColor("Midnight Blue");
            item1.setBrand("Apple");
            item1.setLocation("Central Library - 2nd Floor");
            item1.setIncidentDate(LocalDate.now().minusDays(3));
            item1.setIncidentTime(LocalTime.of(14, 30));
            item1.setStatus(ItemStatus.OPEN);
            item1.setImageUrl("assets/items/macbook.png");
            item1.setAdditionalDetails("GitHub and Linux stickers on bottom case. Username: rahul_sharma");
            itemRepository.save(item1);

            Item item2 = new Item();
            item2.setReporter(student2);
            item2.setItemType(ItemType.FOUND);
            item2.setTitle("Apple MacBook Air Laptop");
            item2.setCategory("Electronics");
            item2.setDescription("Found dark blue Apple laptop in grey sleeve left on table 14 in Library reading section.");
            item2.setColor("Midnight Blue");
            item2.setBrand("Apple");
            item2.setLocation("Central Library - 2nd Floor");
            item2.setIncidentDate(LocalDate.now().minusDays(3));
            item2.setIncidentTime(LocalTime.of(16, 0));
            item2.setStatus(ItemStatus.CLAIM_PENDING);
            item2.setCustodyStatus(CustodyStatus.SECURITY_OFFICE);
            item2.setImageUrl("assets/items/macbook.png");
            itemRepository.save(item2);

            Item item3 = new Item();
            item3.setReporter(student3);
            item3.setItemType(ItemType.LOST);
            item3.setTitle("Brown Leather Bi-fold Wallet");
            item3.setCategory("Wallets");
            item3.setDescription("Brown Titan leather wallet containing college ID card and metro smart card.");
            item3.setColor("Brown");
            item3.setBrand("Titan");
            item3.setLocation("Campus Food Court & Cafeteria");
            item3.setIncidentDate(LocalDate.now().minusDays(2));
            item3.setStatus(ItemStatus.OPEN);
            item3.setImageUrl("assets/items/wallet.png");
            itemRepository.save(item3);

            Item item4 = new Item();
            item4.setReporter(security);
            item4.setItemType(ItemType.FOUND);
            item4.setTitle("Brown Titan Bi-fold Wallet");
            item4.setCategory("Wallets");
            item4.setDescription("Brown leather wallet discovered under table near juice counter in cafeteria.");
            item4.setColor("Brown");
            item4.setBrand("Titan");
            item4.setLocation("Main Cafeteria / Food Court");
            item4.setIncidentDate(LocalDate.now().minusDays(2));
            item4.setStatus(ItemStatus.OPEN);
            item4.setCustodyStatus(CustodyStatus.SECURITY_OFFICE);
            item4.setImageUrl("assets/items/wallet.png");
            itemRepository.save(item4);

            Item item5 = new Item();
            item5.setReporter(student2);
            item5.setItemType(ItemType.FOUND);
            item5.setTitle("Student ID Card - Priya Patel");
            item5.setCategory("ID Cards");
            item5.setDescription("Found student ID card near Seminar Hall 3 podium.");
            item5.setColor("Blue / White");
            item5.setBrand("Campus ID");
            item5.setLocation("Seminar Hall 3");
            item5.setIncidentDate(LocalDate.now().minusDays(5));
            item5.setStatus(ItemStatus.RECOVERED);
            item5.setCustodyStatus(CustodyStatus.ADMIN_OFFICE);
            item5.setImageUrl("assets/items/idcard.png");
            itemRepository.save(item5);

            // 3. Smart Matches
            MatchRecord match1 = new MatchRecord(item1, item2, 92.5,
                    "[\"Exact Category Match: Electronics (+20.0 pts)\", \"Title Similarity: 88% (+22.0 pts)\", \"Exact Color Match: Midnight Blue (+15.0 pts)\", \"Exact Brand Match: Apple (+10.0 pts)\", \"Location Proximity: Central Library (+15.0 pts)\", \"Same Day Incident (+10.0 pts)\"]");
            matchRecordRepository.save(match1);

            MatchRecord match2 = new MatchRecord(item3, item4, 88.0,
                    "[\"Exact Category Match: Wallets (+20.0 pts)\", \"Title Similarity: 82% (+20.5 pts)\", \"Exact Color Match: Brown (+15.0 pts)\", \"Exact Brand Match: Titan (+10.0 pts)\", \"Location Proximity: Cafeteria / Food Court (+12.5 pts)\", \"Same Day Incident (+10.0 pts)\"]");
            matchRecordRepository.save(match2);

            // 4. Claims
            Claim claim1 = new Claim();
            claim1.setItem(item2);
            claim1.setClaimant(student1);
            claim1.setOwnershipDescription("This is my MacBook Air M2 that I left on table 14 while rushing to class.");
            claim1.setPrivateDetails("Lock screen password hint: rahul@, serial number ends with 4821. Octocat sticker underneath.");
            claim1.setStatus(ClaimStatus.PENDING);
            claimRepository.save(claim1);

            Claim claim2 = new Claim();
            claim2.setItem(item5);
            claim2.setClaimant(student2);
            claim2.setOwnershipDescription("My college ID card fell during seminar.");
            claim2.setPrivateDetails("USN 1RV21EC089, Blood Group O+.");
            claim2.setStatus(ClaimStatus.APPROVED);
            claim2.setReviewedBy(admin);
            claim2.setReviewedAt(LocalDateTime.now().minusDays(4));
            claimRepository.save(claim2);

            // 5. Handover
            Handover handover = new Handover();
            handover.setItem(item5);
            handover.setClaim(claim2);
            handover.setRecipient(student2);
            handover.setAuthorizedBy(admin);
            handover.setLocation("Security Main Gate Desk - Admin Wing");
            handover.setVerificationNotes("Student presented original Aadhaar card and verified department enrollment. Handover confirmed.");
            handover.setAckConfirmed(true);
            handover.setHandoverDate(LocalDateTime.now().minusDays(4));
            handoverRepository.save(handover);

            // 6. Notifications
            Notification notif1 = new Notification(student1, "High Confidence Match Found! (92.5%)",
                    "A found item 'Apple MacBook Air Laptop' matching your lost report has been registered.",
                    NotificationType.MATCH_FOUND, item2.getId(), null);
            notificationRepository.save(notif1);

            Notification notif2 = new Notification(student3, "Potential Match Detected (88.0%)",
                    "A found 'Brown Titan Bi-fold Wallet' was reported at Cafeteria. Submit a claim if this is yours.",
                    NotificationType.MATCH_FOUND, item4.getId(), null);
            notificationRepository.save(notif2);

            logger.info("CampusConnect seed data successfully initialized!");
        };
    }
}
