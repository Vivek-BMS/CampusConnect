package com.campusconnect.service;

import com.campusconnect.dto.HandoverRequest;
import com.campusconnect.dto.HandoverResponse;
import com.campusconnect.entity.*;
import com.campusconnect.repository.ClaimRepository;
import com.campusconnect.repository.HandoverRepository;
import com.campusconnect.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HandoverService {

    @Autowired
    private HandoverRepository handoverRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuditLogService auditLogService;

    @Transactional
    public HandoverResponse recordHandover(HandoverRequest request, User authorizedStaff) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found: " + request.getItemId()));

        Claim claim = claimRepository.findById(request.getClaimId())
                .orElseThrow(() -> new RuntimeException("Claim not found: " + request.getClaimId()));

        User recipient = claim.getClaimant();

        Handover handover = new Handover();
        handover.setItem(item);
        handover.setClaim(claim);
        handover.setRecipient(recipient);
        handover.setAuthorizedBy(authorizedStaff);
        handover.setLocation(request.getLocation().trim());
        handover.setVerificationNotes(request.getVerificationNotes());
        handover.setAckConfirmed(request.getAckConfirmed() != null ? request.getAckConfirmed() : true);
        handover.setHandoverDate(LocalDateTime.now());

        Handover savedHandover = handoverRepository.save(handover);

        // Update item status to RECOVERED!
        item.setStatus(ItemStatus.RECOVERED);
        itemRepository.save(item);

        auditLogService.logAction(authorizedStaff, "HANDOVER_COMPLETED", "Handover", savedHandover.getId(),
                "Handover of item '" + item.getTitle() + "' to student " + recipient.getStudentId() + " completed.", null);

        // Send notification to recipient
        notificationService.createNotification(
                recipient,
                "Item Successfully Recovered!",
                "Your lost item '" + item.getTitle() + "' handover was successfully logged and finalized. Status updated to RECOVERED.",
                NotificationType.ITEM_RECOVERED,
                item.getId(),
                claim.getId()
        );

        return HandoverResponse.fromEntity(savedHandover);
    }

    @Transactional(readOnly = true)
    public List<HandoverResponse> getAllHandovers() {
        return handoverRepository.findAllByOrderByHandoverDateDesc().stream()
                .map(HandoverResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HandoverResponse> getHandoversForUser(Long userId) {
        return handoverRepository.findByRecipientIdOrderByHandoverDateDesc(userId).stream()
                .map(HandoverResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
