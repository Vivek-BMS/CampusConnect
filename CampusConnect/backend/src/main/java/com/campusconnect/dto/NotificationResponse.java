package com.campusconnect.dto;

import com.campusconnect.entity.Notification;
import com.campusconnect.entity.NotificationType;
import java.time.LocalDateTime;

public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    private NotificationType notificationType;
    private Long relatedItemId;
    private Long relatedClaimId;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public NotificationResponse() {}

    public static NotificationResponse fromEntity(Notification notification) {
        NotificationResponse resp = new NotificationResponse();
        resp.setId(notification.getId());
        resp.setTitle(notification.getTitle());
        resp.setMessage(notification.getMessage());
        resp.setNotificationType(notification.getNotificationType());
        resp.setRelatedItemId(notification.getRelatedItemId());
        resp.setRelatedClaimId(notification.getRelatedClaimId());
        resp.setIsRead(notification.getIsRead());
        resp.setCreatedAt(notification.getCreatedAt());
        return resp;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getNotificationType() { return notificationType; }
    public void setNotificationType(NotificationType notificationType) { this.notificationType = notificationType; }

    public Long getRelatedItemId() { return relatedItemId; }
    public void setRelatedItemId(Long relatedItemId) { this.relatedItemId = relatedItemId; }

    public Long getRelatedClaimId() { return relatedClaimId; }
    public void setRelatedClaimId(Long relatedClaimId) { this.relatedClaimId = relatedClaimId; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
