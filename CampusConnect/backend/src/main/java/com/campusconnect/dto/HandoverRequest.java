package com.campusconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class HandoverRequest {
    @NotNull(message = "Item ID is required")
    private Long itemId;

    @NotNull(message = "Claim ID is required")
    private Long claimId;

    @NotBlank(message = "Handover location is required")
    private String location;

    private String verificationNotes;
    private Boolean ackConfirmed = true;

    public HandoverRequest() {}

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getVerificationNotes() { return verificationNotes; }
    public void setVerificationNotes(String verificationNotes) { this.verificationNotes = verificationNotes; }

    public Boolean getAckConfirmed() { return ackConfirmed; }
    public void setAckConfirmed(Boolean ackConfirmed) { this.ackConfirmed = ackConfirmed; }
}
