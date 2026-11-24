package com.campusconnect.dto;

import com.campusconnect.entity.MatchRecord;
import java.time.LocalDateTime;

public class MatchResponse {
    private Long id;
    private ItemResponse lostItem;
    private ItemResponse foundItem;
    private Double similarityScore;
    private String matchReasons;
    private String status;
    private LocalDateTime createdAt;

    public MatchResponse() {}

    public static MatchResponse fromEntity(MatchRecord record) {
        MatchResponse resp = new MatchResponse();
        resp.setId(record.getId());
        resp.setLostItem(ItemResponse.fromEntity(record.getLostItem(), false));
        resp.setFoundItem(ItemResponse.fromEntity(record.getFoundItem(), false));
        resp.setSimilarityScore(record.getSimilarityScore());
        resp.setMatchReasons(record.getMatchReasons());
        resp.setStatus(record.getStatus());
        resp.setCreatedAt(record.getCreatedAt());
        return resp;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ItemResponse getLostItem() { return lostItem; }
    public void setLostItem(ItemResponse lostItem) { this.lostItem = lostItem; }

    public ItemResponse getFoundItem() { return foundItem; }
    public void setFoundItem(ItemResponse foundItem) { this.foundItem = foundItem; }

    public Double getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(Double similarityScore) { this.similarityScore = similarityScore; }

    public String getMatchReasons() { return matchReasons; }
    public void setMatchReasons(String matchReasons) { this.matchReasons = matchReasons; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
