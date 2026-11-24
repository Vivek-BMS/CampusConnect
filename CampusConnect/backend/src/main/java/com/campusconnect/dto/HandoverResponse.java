package com.campusconnect.dto;

import com.campusconnect.entity.Handover;
import java.time.LocalDateTime;

public class HandoverResponse {
    private Long id;
    private Long itemId;
    private String itemTitle;
    private Long claimId;
    private Long recipientId;
    private String recipientName;
    private String recipientStudentId;
    private String authorizedByName;
    private LocalDateTime handoverDate;
    private String location;
    private String verificationNotes;
    private Boolean ackConfirmed;

    public HandoverResponse() {}

    public static HandoverResponse fromEntity(Handover handover) {
        HandoverResponse resp = new HandoverResponse();
        resp.setId(handover.getId());
        if (handover.getItem() != null) {
            resp.setItemId(handover.getItem().getId());
            resp.setItemTitle(handover.getItem().getTitle());
        }
        if (handover.getClaim() != null) {
            resp.setClaimId(handover.getClaim().getId());
        }
        if (handover.getRecipient() != null) {
            resp.setRecipientId(handover.getRecipient().getId());
            resp.setRecipientName(handover.getRecipient().getFullName());
            resp.setRecipientStudentId(handover.getRecipient().getStudentId());
        }
        if (handover.getAuthorizedBy() != null) {
            resp.setAuthorizedByName(handover.getAuthorizedBy().getFullName());
        }
        resp.setHandoverDate(handover.getHandoverDate());
        resp.setLocation(handover.getLocation());
        resp.setVerificationNotes(handover.getVerificationNotes());
        resp.setAckConfirmed(handover.getAckConfirmed());
        return resp;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getItemTitle() { return itemTitle; }
    public void setItemTitle(String itemTitle) { this.itemTitle = itemTitle; }

    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }

    public Long getRecipientId() { return recipientId; }
    public void setRecipientId(Long recipientId) { this.recipientId = recipientId; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getRecipientStudentId() { return recipientStudentId; }
    public void setRecipientStudentId(String recipientStudentId) { this.recipientStudentId = recipientStudentId; }

    public String getAuthorizedByName() { return authorizedByName; }
    public void setAuthorizedByName(String authorizedByName) { this.authorizedByName = authorizedByName; }

    public LocalDateTime getHandoverDate() { return handoverDate; }
    public void setHandoverDate(LocalDateTime handoverDate) { this.handoverDate = handoverDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getVerificationNotes() { return verificationNotes; }
    public void setVerificationNotes(String verificationNotes) { this.verificationNotes = verificationNotes; }

    public Boolean getAckConfirmed() { return ackConfirmed; }
    public void setAckConfirmed(Boolean ackConfirmed) { this.ackConfirmed = ackConfirmed; }
}
