package com.campusconnect.dto;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String fullName;
    private String studentId;
    private String email;
    private String role;
    private String department;

    public AuthResponse() {}

    public AuthResponse(String token, Long id, String fullName, String studentId, String email, String role, String department) {
        this.token = token;
        this.id = id;
        this.fullName = fullName;
        this.studentId = studentId;
        this.email = email;
        this.role = role;
        this.department = department;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
