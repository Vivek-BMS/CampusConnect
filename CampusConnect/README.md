# CampusConnect – Intelligent Campus Lost & Found Management System

![CampusConnect Banner](frontend/assets/logo.svg)

> **A modern, responsive, enterprise-grade full-stack web application for college campuses to manage lost and found belongings, calculate smart matching candidates, verify ownership claims, and log physical custody recovery.**

Designed and implemented for university computer science engineering portfolios, final year capstone demonstrations, and campus safety automation.

---

## 📌 Table of Contents
1. [Key Features](#-key-features)
2. [Technology Stack](#-technology-stack)
3. [Architecture Overview](#-architecture-overview)
4. [Smart Matching Algorithm & Scoring Formula](#-smart-matching-algorithm--scoring-formula)
5. [Database Schema & ER Model](#-database-schema--er-model)
6. [API Specification](#-api-specification)
7. [Installation & Setup Instructions](#-installation--setup-instructions)
8. [Demo Test Credentials](#-demo-test-credentials)
9. [UI/UX & SaaS Design Features](#-uiux--saas-design-features)
10. [Bilingual Support (English & Kannada)](#-bilingual-support-english--kannada)
11. [Security & Privacy Safeguards](#-security--privacy-safeguards)

---

## 🌟 Key Features

- **Centralized Campus Repository**: Unified portal for students and staff to register lost articles or report discovered belongings.
- **Smart Weighted Matching Engine**: Java-based algorithm scoring similarity from `0 to 100` across category, title, color, brand, location, date proximity, and description tokens.
- **Explainable Match Breakdown**: Provides exact point attribution and reason tags (e.g. `Exact Category Match (+20 pts)`, `Title Similarity 88% (+22 pts)`).
- **Two-Tier Privacy Protection**: Private identifying marks (lock screen passcode hints, IMEI numbers, inside wallet items) are kept hidden from public browse and only exposed to authorized verifiers during claim inspection.
- **Ownership Verification Workflow**: Multi-state claim lifecycle (`PENDING` ➔ `UNDER_REVIEW` ➔ `APPROVED` / `REJECTED`) with admin review notes.
- **Physical Handover & Custody Tracking**: Records the exact staff member, date/time, security desk location, and acknowledgment before marking items `RECOVERED`.
- **Digital QR Inventory Tagging**: Auto-generates scannable QR inventory labels for found articles deposited in security custody bins.
- **Real-Time In-App Notifications**: Reactive alerts for match detection, claim status updates, and recovery confirmations.
- **SaaS Analytics Dashboard**: Visualizes monthly recovery trends and category distributions using Chart.js.
- **Bilingual Accessibility**: Instant one-click English ⇄ Kannada language toggle across all pages.

---

## 🛠️ Technology Stack

| Layer | Technologies Used |
| :--- | :--- |
| **Frontend** | HTML5, CSS3 (Modern SaaS Design System), JavaScript (ES6+), Bootstrap 5 Grid, Chart.js 4.4, Lucide Icons |
| **Backend** | Java 17 / 21, Spring Boot 3.3.4 (Spring Web, Spring Data JPA, Spring Security 6, Spring Validation) |
| **Authentication** | JSON Web Tokens (JJWT 0.12.5), BCrypt Password Hashing, Role-Based Access Control (RBAC) |
| **Database** | MySQL 8.0+ (Production) / In-Memory H2 (Offline Testing Profile) |
| **Build & Tools** | Apache Maven 3.9+, ZXing 3.5.3 (QR Code Generation), Git |

---

## 🏗️ Architecture Overview

The system strictly follows an enterprise multi-tier layered architecture:

```
CampusConnect/
│
├── frontend/                  # SaaS Responsive Client Interface
│   ├── index.html             # Landing Page (Hero, How It Works, Features, Stats)
│   ├── login.html             # Role-based Login with 1-Click Demo Fill
│   ├── register.html          # Student Registration with USN & Email validation
│   ├── dashboard.html         # Student Dashboard (KPIs, Matches, Reports)
│   ├── report-lost.html       # Report Lost Item Form with Photo Preview
│   ├── report-found.html      # Report Found Item Form with Custody Selection
│   ├── browse-items.html      # Directory with Real-time Search, Filters, Modals
│   ├── admin.html             # Operations Console (Claims, Handovers, Chart.js)
│   ├── css/
│   │   ├── styles.css         # SaaS Tokens (#142B4A Navy, #3B82F6 Sky Blue, etc.)
│   │   └── responsive.css     # Mobile & Tablet Media Queries
│   ├── js/
│   │   ├── api.js             # Fetch Client, JWT Auth Headers, Offline Fallback
│   │   ├── auth.js            # Login/Register Controllers, Session Guards
│   │   ├── app.js             # Global Modals, Toast Alerts, Mobile Drawer
│   │   ├── dashboard.js       # Student Analytics, Match Rendering
│   │   ├── items.js           # Browse Filtering, Item Modals, Claim Submissions
│   │   ├── admin.js           # Chart.js Integration, Claim Approval, Handover Logs
│   │   └── i18n.js            # English & Kannada Translation Dictionary
│   └── assets/                # Logos, SVG Placeholders, Illustrations
│
├── backend/                   # Spring Boot 3.3.4 Application
│   ├── pom.xml                # Maven Dependencies & Plugins
│   └── src/
│       ├── main/
│       │   ├── java/com/campusconnect/
│       │   │   ├── CampusConnectApplication.java
│       │   │   ├── config/        # WebMvcConfig, SecurityConfig, DataInitializer
│       │   │   ├── controller/    # REST API Controllers (Auth, Items, Claims, Admin)
│       │   │   ├── dto/           # Request & Response Data Transfer Objects
│       │   │   ├── entity/        # JPA Entities (User, Item, Claim, Match, Handover)
│       │   │   ├── repository/    # Spring Data JPA Repositories
│       │   │   ├── security/      # JWT Token Provider, Filters, UserPrincipal
│       │   │   └── service/       # Business Logic, Smart Matching, QrCode
│       │   └── resources/
│       │       ├── application.properties     # MySQL Primary Configuration
│       │       └── application-h2.properties  # H2 In-Memory Fallback Profile
│       └── test/                  # Unit and Integration Tests
│
├── database/
│   ├── schema.sql             # Complete DDL Schema with Foreign Keys & Indexes
│   └── data.sql               # Seed Data (Demo Users, Items, Matches, Claims)
│
├── README.md
└── .gitignore
```

---

## 🧠 Smart Matching Algorithm & Scoring Formula

The matching engine is implemented in Java (`com.campusconnect.service.SmartMatchingService`). It evaluates candidate pairs of `LOST` and `FOUND` reports whenever an item is submitted.

### Mathematical Formula:
$$\text{Score} = W_{\text{cat}} + W_{\text{title}} + W_{\text{color}} + W_{\text{brand}} + W_{\text{loc}} + W_{\text{date}} + W_{\text{desc}}$$

Maximum achievable score: **100.0 Points**.

| Parameter | Weight | Method of Computation |
| :--- | :---: | :--- |
| **Category** | **20.0 pts** | Exact case-insensitive category equality. |
| **Title Similarity** | **25.0 pts** | $\max(\text{Token Overlap}, \text{Normalized Levenshtein Distance}) \times 25.0$. |
| **Colour Match** | **15.0 pts** | Color token intersection (e.g., "Midnight Blue" vs "Blue"). |
| **Brand Match** | **10.0 pts** | String similarity on normalized brand names (e.g., "Apple", "Titan"). |
| **Location Match** | **15.0 pts** | Building / Room / Campus area token proximity. |
| **Date Proximity** | **10.0 pts** | Same day = 10 pts; 1–2 days = 9 pts; 3–7 days = 6 pts; 8–14 days = 3 pts. |
| **Description Keywords** | **5.0 pts** | Jaccard index of extracted keywords after stop-word removal. |

### Configurable Weights
Weights are externalized in `application.properties`:
```properties
app.matching.weight.category=20.0
app.matching.weight.title=25.0
app.matching.weight.color=15.0
app.matching.weight.brand=10.0
app.matching.weight.location=15.0
app.matching.weight.date=10.0
app.matching.weight.description=5.0
app.matching.threshold=50.0
```

> [!IMPORTANT]
> A high similarity score is an **algorithmic indicator** to alert students, **not legal proof of ownership**. The student must submit confidential private identifying marks (e.g., lock screen passcode hints, serial numbers) for administrative verification before physical handover.

---

## 🗄️ Database Schema & ER Model

The relational database is normalized up to **3NF** across 7 tables:

1. `users`: Stores students, campus security staff, and system administrators with hashed credentials and college USN/Employee IDs.
2. `items`: Stores lost and found belongings with category, public description, custody location, and private identifying marks.
3. `claims`: Tracks ownership claims filed by students against found items.
4. `matches`: Records candidate pairs evaluated by the Java Smart Matching engine with similarity scores and reason logs.
5. `notifications`: Stores user-specific reactive notifications (read/unread).
6. `handovers`: Audit record of physical item transfer, authorized staff member, and date/time.
7. `audit_logs`: Immutable trail of system operations for security and compliance.

---

## 📡 API Specification

### Authentication & Users
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/register` | Public | Register student with USN and college email |
| `POST` | `/api/v1/auth/login` | Public | Authenticate user and issue JWT Bearer token |
| `GET` | `/api/v1/auth/me` | Authenticated | Retrieve current authenticated user profile |

### Lost & Found Items
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/items/report-lost` | Student / Staff | Report lost belonging and initiate matching |
| `POST` | `/api/v1/items/report-found` | Student / Staff | Report found item and generate QR label |
| `GET` | `/api/v1/items/browse` | Public | Filter items by category, type, date, location |
| `GET` | `/api/v1/items/{id}` | Public | View item details (anonymized for public) |
| `GET` | `/api/v1/items/my-reports` | Authenticated | Get reports filed by the logged-in user |
| `GET` | `/api/v1/items/categories` | Public | List standardized item categories |

### Smart Matching & Claims
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/matches/my-matches` | Authenticated | Get potential matches for user's reports |
| `POST` | `/api/v1/claims` | Student | Submit ownership claim with secret proof |
| `GET` | `/api/v1/claims/my-claims` | Authenticated | Get claims submitted by the user |
| `PATCH`| `/api/v1/claims/{id}/status` | Admin / Security | Approve or reject claim with admin notes |

### Handover & Administration
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/handovers` | Admin / Security | Finalize physical handover & set status to `RECOVERED` |
| `GET` | `/api/v1/handovers` | Admin / Security | View physical handover history |
| `GET` | `/api/v1/admin/stats` | Admin / Security | Fetch aggregated KPIs and Chart.js datasets |
| `GET` | `/api/v1/admin/audit-logs` | Admin / Security | Inspect system-wide audit trail |

---

## 🚀 Installation & Setup Instructions

### Prerequisites
- **Java**: JDK 17 or JDK 21 installed (`java -version`)
- **Maven**: Version 3.8+ (`mvn -version`)
- **MySQL**: Version 8.0+ (`mysql --version`)
- **Modern Browser**: Chrome, Firefox, Edge, or Safari

### Step 1: Database Setup
Open MySQL command line or MySQL Workbench:
```sql
CREATE DATABASE campusconnect_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
Import schema and seed data:
```bash
mysql -u root -p campusconnect_db < database/schema.sql
mysql -u root -p campusconnect_db < database/data.sql
```

### Step 2: Configure Backend Properties
Edit `backend/src/main/resources/application.properties` to match your MySQL password:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campusconnect_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

*(Optional)* To run with the in-memory **H2 Database** without MySQL:
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

### Step 3: Run Spring Boot Backend
```bash
cd backend
mvn clean package -DskipTests
mvn spring-boot:run
```
The REST API server will start on `http://localhost:8080`.

### Step 4: Open Frontend
You can open `frontend/index.html` directly in any web browser, or serve it via Live Server / Python HTTP / VS Code:
```bash
cd frontend
python -m http.server 5500
```
Visit `http://localhost:5500` or `http://localhost:8080`!

---

## 🔑 Demo Test Credentials

To facilitate rapid demonstration without manual registration:

| Role | Email | Password | Permissions |
| :--- | :--- | :--- | :--- |
| **System Administrator** | `admin@campus.edu` | `Admin@123` | Full claim verification, handovers, audit logs |
| **Security Staff** | `security@campus.edu` | `Security@123` | Handover logging, custody management |
| **Student (Rahul Sharma)** | `rahul.cs@campus.edu` | `Student@123` | Report lost/found, view matches, file claims |
| **Student (Priya Patel)** | `priya.ec@campus.edu` | `Student@123` | Student portal, report belongings |

*(Tip: On the Sign In page, use the **1-Click Demo Accounts** buttons to populate credentials automatically!)*

---

## 🎨 UI/UX & SaaS Design System

- **Primary Brand Color**: Navy Blue `#142B4A` (Executive, institutional authority).
- **Accent Color**: Sky Blue `#3B82F6` (Interactive links, primary action triggers).
- **Status Indicators**:
  - `RECOVERED` / Approved: Emerald Green `#10B981`
  - `PENDING` / In Review: Amber Orange `#F59E0B`
  - `REJECTED` / Urgent: Crimson Red `#EF4444`
- **Surface**: Pure White Cards `#FFFFFF` on Light Grey Canvas `#F5F7FA`.
- **Card Styling**: 12px–16px rounded borders, subtle multi-layer drop shadows (`rgba(20, 43, 74, 0.08)`).
- **Interactions**: Smooth hover elevations (`transform: translateY(-4px)`), animated toast notices, responsive drawer sidebar.

---

## 🌐 Bilingual Support (English & Kannada)

CampusConnect features built-in internationalization (`js/i18n.js`). Users can click the **🌐 ಕನ್ನಡ / English** pill in the top navigation bar to toggle translations dynamically without reloading the page.

---

## 🛡️ Security & Privacy Safeguards

1. **Password Encryption**: All passwords hashed via Spring Security's `BCryptPasswordEncoder` with 10 salt rounds.
2. **Stateless JWT Tokens**: Cryptographically signed HS256 tokens with 24-hour expiration.
3. **Role-Based API Guarding**: Admin endpoints (`/api/v1/admin/**`, `/api/v1/handovers/**`) strictly restricted to `ROLE_ADMIN` and `ROLE_SECURITY_STAFF`.
4. **Anonymized Public Listings**: Public browse cards do not disclose claimant contact details or student IDs.
5. **Secure Multipart Validation**: File uploads restricted to images (`jpg`, `png`, `webp`) under 10MB to protect against malicious uploads.

---

## 📜 Academic Project Notice
This project is developed as a complete software engineering portfolio reference adhering to clean architecture, REST API standards, and database normalization guidelines.
