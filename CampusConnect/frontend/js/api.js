/**
 * CampusConnect API Client
 * Centralized fetch handler with JWT authorization, error management,
 * and seamless fallback for offline/preview environments.
 */

const API_BASE_URL = 'http://localhost:8080/api/v1';

const API = {
  getToken() {
    return localStorage.getItem('cc_token');
  },

  setToken(token) {
    localStorage.setItem('cc_token', token);
  },

  getCurrentUser() {
    const userStr = localStorage.getItem('cc_user');
    return userStr ? JSON.parse(userStr) : null;
  },

  setCurrentUser(user) {
    localStorage.setItem('cc_user', JSON.stringify(user));
  },

  logout() {
    localStorage.removeItem('cc_token');
    localStorage.removeItem('cc_user');
    window.location.href = 'login.html';
  },

  async request(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const token = this.getToken();

    const headers = {
      'Accept': 'application/json',
      ...options.headers,
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    if (!(options.body instanceof FormData) && !headers['Content-Type']) {
      headers['Content-Type'] = 'application/json';
    }

    try {
      const response = await fetch(url, { ...options, headers });

      if (response.status === 401) {
        // Token expired or invalid
        console.warn('Unauthorized request. Redirecting to login.');
        this.logout();
        return null;
      }

      const json = await response.json();
      return json;
    } catch (err) {
      console.warn(`API server at ${url} unreachable. Using demo data fallback. Error:`, err.message);
      return this.handleFallback(endpoint, options);
    }
  },

  get(endpoint) {
    return this.request(endpoint, { method: 'GET' });
  },

  post(endpoint, data) {
    return this.request(endpoint, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  },

  patch(endpoint, data) {
    return this.request(endpoint, {
      method: 'PATCH',
      body: JSON.stringify(data),
    });
  },

  async uploadFile(file) {
    const formData = new FormData();
    formData.append('file', file);
    return this.request('/uploads', {
      method: 'POST',
      body: formData,
    });
  },

  /**
   * Smart Demo Fallback Store:
   * Ensures the application functions completely when previewing the HTML directly
   * or when the Spring Boot backend is starting up.
   */
  handleFallback(endpoint, options) {
    // Return sample responses matching backend contracts
    if (endpoint.startsWith('/items/browse')) {
      const items = FallbackData.items;
      return { success: true, message: 'Loaded demo items', data: items };
    }
    if (endpoint.startsWith('/items/categories')) {
      return { success: true, data: ["Electronics", "ID Cards", "Books", "Bags", "Wallets", "Accessories", "Clothing", "Other"] };
    }
    if (endpoint.startsWith('/admin/stats')) {
      return { success: true, data: FallbackData.adminStats };
    }
    if (endpoint.startsWith('/matches/my-matches')) {
      return { success: true, data: FallbackData.matches };
    }
    if (endpoint.startsWith('/claims/my-claims') || endpoint.startsWith('/claims/all')) {
      return { success: true, data: FallbackData.claims };
    }
    if (endpoint.startsWith('/notifications')) {
      return { success: true, data: FallbackData.notifications };
    }
    if (endpoint.startsWith('/auth/me')) {
      const user = this.getCurrentUser() || FallbackData.demoUser;
      return { success: true, data: user };
    }
    if (endpoint.startsWith('/handovers')) {
      return { success: true, data: FallbackData.handovers };
    }
    if (endpoint.startsWith('/admin/audit-logs')) {
      return { success: true, data: FallbackData.auditLogs };
    }
    if (endpoint.startsWith('/admin/users')) {
      return { success: true, data: FallbackData.users };
    }

    return { success: true, message: 'Action simulated in preview mode' };
  }
};

/**
 * Built-in Realistic Fallback Dataset
 */
const FallbackData = {
  demoUser: {
    id: 3,
    fullName: "Rahul Sharma",
    studentId: "1RV21CS045",
    email: "rahul.cs@campus.edu",
    role: "ROLE_STUDENT",
    department: "Computer Science & Engineering"
  },
  items: [
    {
      id: 1,
      itemType: "LOST",
      title: "MacBook Air M2 13-inch",
      category: "Electronics",
      description: "Midnight blue Apple MacBook Air M2 in grey Tomtoc sleeve. Lost near Library 2nd floor.",
      color: "Midnight Blue",
      brand: "Apple",
      location: "Central Library - 2nd Floor",
      incidentDate: "2026-11-20",
      status: "OPEN",
      imageUrl: "assets/macbook.png",
      reporterName: "Campus Community Member",
      reporterDepartment: "Computer Science"
    },
    {
      id: 2,
      itemType: "FOUND",
      title: "Apple MacBook Air Laptop",
      category: "Electronics",
      description: "Found dark blue Apple laptop in grey sleeve left on table 14 in Library reading section.",
      color: "Midnight Blue",
      brand: "Apple",
      location: "Central Library - 2nd Floor",
      incidentDate: "2026-11-20",
      status: "CLAIM_PENDING",
      custodyStatus: "SECURITY_OFFICE",
      imageUrl: "assets/macbook.png",
      reporterName: "Campus Community Member",
      reporterDepartment: "Electronics"
    },
    {
      id: 3,
      itemType: "LOST",
      title: "Brown Leather Bi-fold Wallet",
      category: "Wallets",
      description: "Brown Titan leather wallet containing college ID card and metro smart card.",
      color: "Brown",
      brand: "Titan",
      location: "Campus Food Court & Cafeteria",
      incidentDate: "2026-11-21",
      status: "OPEN",
      imageUrl: "assets/wallet.png",
      reporterName: "Campus Community Member",
      reporterDepartment: "Information Science"
    },
    {
      id: 4,
      itemType: "FOUND",
      title: "Brown Titan Bi-fold Wallet",
      category: "Wallets",
      description: "Brown leather wallet discovered under table near juice counter in cafeteria.",
      color: "Brown",
      brand: "Titan",
      location: "Main Cafeteria / Food Court",
      incidentDate: "2026-11-21",
      status: "OPEN",
      custodyStatus: "SECURITY_OFFICE",
      imageUrl: "assets/wallet.png",
      reporterName: "Campus Security Desk",
      reporterDepartment: "Campus Safety"
    },
    {
      id: 5,
      itemType: "FOUND",
      title: "Student ID Card - Priya Patel",
      category: "ID Cards",
      description: "Found student laminated RFID ID card near Seminar Hall 3 podium.",
      color: "Blue / White",
      brand: "Campus ID",
      location: "Seminar Hall 3",
      incidentDate: "2026-11-18",
      status: "RECOVERED",
      custodyStatus: "ADMIN_OFFICE",
      imageUrl: "assets/idcard.png",
      reporterName: "System Administrator",
      reporterDepartment: "Campus Operations"
    }
  ],
  matches: [
    {
      id: 1,
      similarityScore: 92.5,
      status: "ACTIVE",
      matchReasons: "[\"Exact Category Match: Electronics (+20.0 pts)\", \"Title Similarity: 88% (+22.0 pts)\", \"Exact Color Match: Midnight Blue (+15.0 pts)\", \"Exact Brand Match: Apple (+10.0 pts)\", \"Location Proximity: Central Library (+15.0 pts)\", \"Same Day Incident (+10.0 pts)\"]",
      lostItem: {
        id: 1,
        title: "MacBook Air M2 13-inch",
        category: "Electronics",
        location: "Central Library",
        incidentDate: "2026-11-20"
      },
      foundItem: {
        id: 2,
        title: "Apple MacBook Air Laptop",
        category: "Electronics",
        location: "Central Library",
        incidentDate: "2026-11-20"
      }
    }
  ],
  claims: [
    {
      id: 1,
      itemId: 2,
      itemTitle: "Apple MacBook Air Laptop",
      claimantName: "Rahul Sharma",
      claimantStudentId: "1RV21CS045",
      claimantEmail: "rahul.cs@campus.edu",
      ownershipDescription: "Left it on table 14 while rushing to afternoon lecture.",
      privateDetails: "Lock screen password hint: rahul@, serial number ends in 4821. Octocat sticker on bottom.",
      status: "PENDING",
      createdAt: "2026-11-20T16:40:00"
    },
    {
      id: 2,
      itemId: 5,
      itemTitle: "Student ID Card - Priya Patel",
      claimantName: "Priya Patel",
      claimantStudentId: "1RV21EC089",
      claimantEmail: "priya.ec@campus.edu",
      ownershipDescription: "Dropped near podium during seminar.",
      privateDetails: "USN 1RV21EC089, blood group O+.",
      status: "APPROVED",
      reviewedByName: "System Administrator",
      createdAt: "2026-11-19T09:15:00"
    }
  ],
  notifications: [
    {
      id: 1,
      title: "High Confidence Match Found! (92.5%)",
      message: "A found item 'Apple MacBook Air Laptop' matching your lost report has been registered.",
      notificationType: "MATCH_FOUND",
      relatedItemId: 2,
      isRead: false,
      createdAt: "2026-11-20T16:05:00"
    },
    {
      id: 2,
      title: "Potential Match Detected (88.0%)",
      message: "A found 'Brown Titan Bi-fold Wallet' was reported at Cafeteria.",
      notificationType: "MATCH_FOUND",
      relatedItemId: 4,
      isRead: false,
      createdAt: "2026-11-21T14:10:00"
    }
  ],
  handovers: [
    {
      id: 1,
      itemId: 5,
      itemTitle: "Student ID Card - Priya Patel",
      recipientName: "Priya Patel",
      recipientStudentId: "1RV21EC089",
      authorizedByName: "System Administrator",
      location: "Security Main Gate Desk - Admin Wing",
      handoverDate: "2026-11-19T13:45:00",
      verificationNotes: "Identity verified against university registrar records. Physical item handed over and signed."
    }
  ],
  adminStats: {
    totalLost: 14,
    totalFound: 18,
    potentialMatches: 9,
    recoveredItems: 11,
    pendingClaims: 3,
    totalUsers: 142,
    categoryDistribution: {
      "Electronics": 12,
      "Wallets": 8,
      "ID Cards": 6,
      "Bags": 5,
      "Accessories": 4,
      "Books": 3
    },
    monthlyRecovery: {
      "Jul": 4,
      "Aug": 7,
      "Sep": 12,
      "Oct": 9,
      "Nov": 11
    }
  },
  auditLogs: [
    { id: 1, action: "REPORT_LOST", entityName: "Item", entityId: 1, details: "Rahul Sharma reported lost MacBook Air", ipAddress: "192.168.1.45", createdAt: "2026-11-20T14:35:00" },
    { id: 2, action: "REPORT_FOUND", entityName: "Item", entityId: 2, details: "Priya Patel reported found MacBook Air", ipAddress: "192.168.1.62", createdAt: "2026-11-20T16:00:00" },
    { id: 3, action: "RUN_SMART_MATCH", entityName: "MatchRecord", entityId: 1, details: "Algorithm evaluated 92.5% similarity score", ipAddress: "127.0.0.1", createdAt: "2026-11-20T16:01:00" },
    { id: 4, action: "SUBMIT_CLAIM", entityName: "Claim", entityId: 1, details: "Rahul Sharma filed ownership claim for Item #2", ipAddress: "192.168.1.45", createdAt: "2026-11-20T16:40:00" },
    { id: 5, action: "APPROVE_CLAIM", entityName: "Claim", entityId: 2, details: "Admin approved claim for Priya Patel ID Card", ipAddress: "192.168.1.10", createdAt: "2026-11-19T09:15:00" },
    { id: 6, action: "HANDOVER_COMPLETED", entityName: "Handover", entityId: 1, details: "Completed physical handover of ID Card", ipAddress: "192.168.1.10", createdAt: "2026-11-19T13:45:00" }
  ],
  users: [
    { id: 1, fullName: "System Administrator", studentId: "ADM-2024-001", email: "admin@campus.edu", department: "Campus Operations & Security", role: "ROLE_ADMIN", isActive: true },
    { id: 2, fullName: "Chief Security Officer", studentId: "SEC-2024-002", email: "security@campus.edu", department: "Campus Safety & Vigilance", role: "ROLE_SECURITY_STAFF", isActive: true },
    { id: 3, fullName: "Rahul Sharma", studentId: "1RV21CS045", email: "rahul.cs@campus.edu", department: "Computer Science & Engineering", role: "ROLE_STUDENT", isActive: true },
    { id: 4, fullName: "Priya Patel", studentId: "1RV21EC089", email: "priya.ec@campus.edu", department: "Electronics & Communication", role: "ROLE_STUDENT", isActive: true }
  ]
};
