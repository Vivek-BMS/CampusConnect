package com.campusconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"lost_item_id", "found_item_id"})
})
public class MatchRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lost_item_id", nullable = false)
    private Item lostItem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "found_item_id", nullable = false)
    private Item foundItem;

    @Column(name = "similarity_score", nullable = false)
    private Double similarityScore;

    @Column(name = "match_reasons", nullable = false, columnDefinition = "TEXT")
    private String matchReasons; // JSON or human-readable explanation

    @Column(nullable = false, length = 30)
    private String status = "ACTIVE"; // ACTIVE, DISMISSED, CONFIRMED

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public MatchRecord() {}

    public MatchRecord(Item lostItem, Item foundItem, Double similarityScore, String matchReasons) {
        this.lostItem = lostItem;
        this.foundItem = foundItem;
        this.similarityScore = similarityScore;
        this.matchReasons = matchReasons;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Item getLostItem() { return lostItem; }
    public void setLostItem(Item lostItem) { this.lostItem = lostItem; }

    public Item getFoundItem() { return foundItem; }
    public void setFoundItem(Item foundItem) { this.foundItem = foundItem; }

    public Double getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(Double similarityScore) { this.similarityScore = similarityScore; }

    public String getMatchReasons() { return matchReasons; }
    public void setMatchReasons(String matchReasons) { this.matchReasons = matchReasons; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
