package com.campusconnect.dto;

import com.campusconnect.entity.ClaimStatus;
import jakarta.validation.constraints.NotNull;

public class ClaimStatusUpdateRequest {
    @NotNull(message = "Claim status is required")
    private ClaimStatus status;

    private String adminNotes;

    public ClaimStatusUpdateRequest() {}

    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
}
