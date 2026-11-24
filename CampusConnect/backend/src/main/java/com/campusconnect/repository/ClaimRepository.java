package com.campusconnect.repository;

import com.campusconnect.entity.Claim;
import com.campusconnect.entity.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByItemIdOrderByCreatedAtDesc(Long itemId);
    List<Claim> findByClaimantIdOrderByCreatedAtDesc(Long claimantId);
    List<Claim> findByStatusOrderByCreatedAtDesc(ClaimStatus status);
    long countByStatus(ClaimStatus status);
    boolean existsByItemIdAndClaimantId(Long itemId, Long claimantId);
}
