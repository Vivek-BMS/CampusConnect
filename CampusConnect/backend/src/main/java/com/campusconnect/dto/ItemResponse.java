package com.campusconnect.dto;

import com.campusconnect.entity.CustodyStatus;
import com.campusconnect.entity.Item;
import com.campusconnect.entity.ItemStatus;
import com.campusconnect.entity.ItemType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ItemResponse {
    private Long id;
    private ItemType itemType;
    private String title;
    private String category;
    private String description;
    private String color;
    private String brand;
    private String location;
    private LocalDate incidentDate;
    private LocalTime incidentTime;
    private String imageUrl;
    private ItemStatus status;
    private CustodyStatus custodyStatus;
    private Long reporterId;
    private String reporterName;
    private String reporterDepartment;
    private LocalDateTime createdAt;
    private String qrCodeData; // Base64 QR code representation for found items

    public ItemResponse() {}

    public static ItemResponse fromEntity(Item item, boolean includePrivateDetails) {
        ItemResponse resp = new ItemResponse();
        resp.setId(item.getId());
        resp.setItemType(item.getItemType());
        resp.setTitle(item.getTitle());
        resp.setCategory(item.getCategory());
        resp.setDescription(item.getDescription());
        resp.setColor(item.getColor());
        resp.setBrand(item.getBrand());
        resp.setLocation(item.getLocation());
        resp.setIncidentDate(item.getIncidentDate());
        resp.setIncidentTime(item.getIncidentTime());
        resp.setImageUrl(item.getImageUrl());
        resp.setStatus(item.getStatus());
        resp.setCustodyStatus(item.getCustodyStatus());
        resp.setCreatedAt(item.getCreatedAt());

        if (item.getReporter() != null) {
            resp.setReporterId(item.getReporter().getId());
            // Anonymize or protect student details on public browse as per security guidelines
            if (includePrivateDetails) {
                resp.setReporterName(item.getReporter().getFullName());
                resp.setReporterDepartment(item.getReporter().getDepartment());
            } else {
                resp.setReporterName("Campus Community Member");
                resp.setReporterDepartment(item.getReporter().getDepartment());
            }
        }
        return resp;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ItemType getItemType() { return itemType; }
    public void setItemType(ItemType itemType) { this.itemType = itemType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDate getIncidentDate() { return incidentDate; }
    public void setIncidentDate(LocalDate incidentDate) { this.incidentDate = incidentDate; }

    public LocalTime getIncidentTime() { return incidentTime; }
    public void setIncidentTime(LocalTime incidentTime) { this.incidentTime = incidentTime; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus status) { this.status = status; }

    public CustodyStatus getCustodyStatus() { return custodyStatus; }
    public void setCustodyStatus(CustodyStatus custodyStatus) { this.custodyStatus = custodyStatus; }

    public Long getReporterId() { return reporterId; }
    public void setReporterId(Long reporterId) { this.reporterId = reporterId; }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }

    public String getReporterDepartment() { return reporterDepartment; }
    public void setReporterDepartment(String reporterDepartment) { this.reporterDepartment = reporterDepartment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getQrCodeData() { return qrCodeData; }
    public void setQrCodeData(String qrCodeData) { this.qrCodeData = qrCodeData; }
}
