package com.campusconnect.dto;

import com.campusconnect.entity.CustodyStatus;
import com.campusconnect.entity.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class ItemRequest {
    @NotNull(message = "Item type is required (LOST or FOUND)")
    private ItemType itemType;

    @NotBlank(message = "Item name / title is required")
    private String title;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Description is required")
    private String description;

    private String color;
    private String brand;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Incident date is required")
    private LocalDate incidentDate;

    private LocalTime incidentTime;
    private String imageUrl;
    private CustodyStatus custodyStatus;
    private String additionalDetails;

    public ItemRequest() {}

    // Getters and setters
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

    public CustodyStatus getCustodyStatus() { return custodyStatus; }
    public void setCustodyStatus(CustodyStatus custodyStatus) { this.custodyStatus = custodyStatus; }

    public String getAdditionalDetails() { return additionalDetails; }
    public void setAdditionalDetails(String additionalDetails) { this.additionalDetails = additionalDetails; }
}
