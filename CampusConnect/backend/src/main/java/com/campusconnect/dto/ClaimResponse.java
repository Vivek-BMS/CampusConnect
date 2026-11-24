package com.campusconnect.dto;

import com.campusconnect.entity.Claim;
import com.campusconnect.entity.ClaimStatus;
import java.time.LocalDateTime;

public class ClaimResponse {
    private Long id;
    private Long itemId;
    private String itemTitle;
    private String itemCategory;
    private Long claimantId;
    private String claimantName;
    private String claimantEmail;
    private String claimantStudentId;
    private String ownershipDescription;
    private String privateDetails;
    private String proofImageUrl;
    private ClaimStatus status;
    private String adminNotes;
    private String reviewedByName;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;

    public ClaimResponse() {}

    public static ClaimResponse fromEntity(Claim claim, boolean isStaffOrOwner) {
        ClaimResponse resp = new ClaimResponse();
        resp.setId(claim.getId());
        if (claim.getItem() != null) {
            resp.setItemId(claim.getItem().getId());
            resp.setItemTitle(claim.getItem().getTitle());
            resp.setItemCategory(claim.getItem().getCategory());
        }
        if (claim.getClaimant() != null) {
            resp.setClaimantId(claim.getClaimant().getId());
            if (isStaffOrOwner) {
                resp.setClaimantName(claim.getClaimant().getFullName());
                resp.setClaimantEmail(claim.getClaimant().getEmail());
                resp.setClaimantStudentId(claim.getClaimant().getStudentId());
            } else {
                resp.setClaimantName("Registered Student");
            }
        }
        resp.setOwnershipDescription(claim.getOwnershipDescription());
        // Private details only visible to staff or claimant
        if (isStaffOrOwner) {
            resp.setPrivateDetails(claim.getPrivateDetails());
        }
        resp.setProofImageUrl(claim.getProofImageUrl());
        resp.setStatus(claim.getStatus());
        resp.setAdminNotes(claim.getAdminNotes());
        if (claim.getReviewedBy() != null) {
            resp.setReviewedByName(claim.getReviewedBy().getFullName());
        }
        resp.setReviewedAt(claim.getReviewedAt());
        resp.setCreatedAt(claim.getCreatedAt());
        return resp;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getItemTitle() { return itemTitle; }
    public void setItemTitle(String itemTitle) { this.itemTitle = itemTitle; }

    public String getItemCategory() { return itemCategory; }
    public void setCategory(String itemCategory) { this.itemCategory = itemCategory; }

    public Long getClaimantId() { return claimantId; }
    public void setClaimantId(Long claimantId) { this.claimantId = claimantId; }

    public String getClaimantName() { return claimantName; }
    public void setClaimantName(String claimantName) { this.claimantName = claimantName; }

    public String getClaimantEmail() { return claimantEmail; }
    public void setClaimantEmail(String claimantEmail) { this.claimantEmail = claimantEmail; }

    public String getClaimantStudentId() { return claimantStudentId; }
    public void setClaimantStudentId(String claimantStudentId) { this.claimantStudentId = claimantStudentId; }

    public String getOwnershipDescription() { return ownershipDescription; }
    public void setOwnershipDescription(String ownershipDescription) { this.ownershipDescription = ownershipDescription; }

    public String getPrivateDetails() { return privateDetails; }
    public void setPrivateDetails(String privateDetails) { this.privateDetails = privateDetails; }

    public String getProofImageUrl() { return proofImageUrl; }
    public void setProofImageUrl(String proofImageUrl) { this.proofImageUrl = proofImageUrl; }

    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public String getReviewedByName() { return reviewedByName; }
    public void setReviewedByName(String reviewedByName) { this.reviewedByName = reviewedByName; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
