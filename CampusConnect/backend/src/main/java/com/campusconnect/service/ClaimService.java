package com.campusconnect.service;

import com.campusconnect.dto.ClaimRequest;
import com.campusconnect.dto.ClaimResponse;
import com.campusconnect.dto.ClaimStatusUpdateRequest;
import com.campusconnect.entity.*;
import com.campusconnect.repository.ClaimRepository;
import com.campusconnect.repository.ItemRepository;
import com.campusconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClaimService {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuditLogService auditLogService;

    @Transactional
    public ClaimResponse submitClaim(ClaimRequest request, Long claimantId) {
        User claimant = userRepository.findById(claimantId)
                .orElseThrow(() -> new RuntimeException("Claimant not found"));

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found: " + request.getItemId()));

        if (item.getStatus() == ItemStatus.RECOVERED || item.getStatus() == ItemStatus.CLOSED) {
            throw new IllegalStateException("Cannot claim an item that has already been recovered or closed");
        }

        if (claimRepository.existsByItemIdAndClaimantId(item.getId(), claimantId)) {
            throw new IllegalArgumentException("You have already submitted a claim for this item");
        }

        Claim claim = new Claim();
        claim.setItem(item);
        claim.setClaimant(claimant);
        claim.setOwnershipDescription(request.getOwnershipDescription().trim());
        claim.setPrivateDetails(request.getPrivateDetails().trim());
        claim.setProofImageUrl(request.getProofImageUrl());
        claim.setStatus(ClaimStatus.PENDING);

        Claim savedClaim = claimRepository.save(claim);

        // Update item status to CLAIM_PENDING
        if (item.getStatus() == ItemStatus.OPEN) {
            item.setStatus(ItemStatus.CLAIM_PENDING);
            itemRepository.save(item);
        }

        auditLogService.logAction(claimant, "SUBMIT_CLAIM", "Claim", savedClaim.getId(),
                "Ownership claim submitted for item: " + item.getTitle(), null);

        // Notify admins / staff (or notification to user confirming submission)
        notificationService.createNotification(
                claimant,
                "Claim Submitted Successfully",
                "Your ownership claim for item '" + item.getTitle() + "' has been submitted and is awaiting administrator verification.",
                NotificationType.CLAIM_SUBMITTED,
                item.getId(),
                savedClaim.getId()
        );

        return ClaimResponse.fromEntity(savedClaim, true);
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> getClaimsByItem(Long itemId, boolean isStaff) {
        return claimRepository.findByItemIdOrderByCreatedAtDesc(itemId).stream()
                .map(c -> ClaimResponse.fromEntity(c, isStaff))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> getClaimsByClaimant(Long claimantId) {
        return claimRepository.findByClaimantIdOrderByCreatedAtDesc(claimantId).stream()
                .map(c -> ClaimResponse.fromEntity(c, true))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> getAllClaims(ClaimStatus status) {
        List<Claim> claims = (status != null) ?
                claimRepository.findByStatusOrderByCreatedAtDesc(status) :
                claimRepository.findAll();

        return claims.stream()
                .map(c -> ClaimResponse.fromEntity(c, true))
                .collect(Collectors.toList());
    }

    @Transactional
    public ClaimResponse updateClaimStatus(Long claimId, ClaimStatusUpdateRequest request, User reviewer) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found: " + claimId));

        claim.setStatus(request.getStatus());
        claim.setAdminNotes(request.getAdminNotes());
        claim.setReviewedBy(reviewer);
        claim.setReviewedAt(LocalDateTime.now());

        Claim updatedClaim = claimRepository.save(claim);
        Item item = claim.getItem();

        // If approved, set item to CLAIM_APPROVED
        if (request.getStatus() == ClaimStatus.APPROVED) {
            item.setStatus(ItemStatus.CLAIM_APPROVED);
            itemRepository.save(item);

            // Notify claimant
            notificationService.createNotification(
                    claim.getClaimant(),
                    "Claim Approved!",
                    "Congratulations! Your ownership claim for '" + item.getTitle() + "' has been APPROVED. Please visit the Campus Safety Desk for physical verification and handover.",
                    NotificationType.CLAIM_APPROVED,
                    item.getId(),
                    claim.getId()
            );
        } else if (request.getStatus() == ClaimStatus.REJECTED) {
            // Check if there are other pending claims
            List<Claim> remainingClaims = claimRepository.findByItemIdOrderByCreatedAtDesc(item.getId());
            boolean hasOtherPending = remainingClaims.stream().anyMatch(c -> c.getStatus() == ClaimStatus.PENDING || c.getStatus() == ClaimStatus.UNDER_REVIEW);
            if (!hasOtherPending) {
                item.setStatus(ItemStatus.OPEN);
                itemRepository.save(item);
            }

            // Notify claimant
            notificationService.createNotification(
                    claim.getClaimant(),
                    "Claim Status: Rejected",
                    "Your ownership claim for '" + item.getTitle() + "' could not be verified. Note: " + (request.getAdminNotes() != null ? request.getAdminNotes() : "Details provided did not match item records."),
                    NotificationType.CLAIM_REJECTED,
                    item.getId(),
                    claim.getId()
            );
        }

        auditLogService.logAction(reviewer, "REVIEW_CLAIM", "Claim", claimId,
                "Claim status changed to " + request.getStatus() + " for claimant: " + claim.getClaimant().getStudentId(), null);

        return ClaimResponse.fromEntity(updatedClaim, true);
    }
}
