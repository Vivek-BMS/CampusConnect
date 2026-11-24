package com.campusconnect.repository;

import com.campusconnect.entity.MatchRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRecordRepository extends JpaRepository<MatchRecord, Long> {

    Optional<MatchRecord> findByLostItemIdAndFoundItemId(Long lostItemId, Long foundItemId);

    boolean existsByLostItemIdAndFoundItemId(Long lostItemId, Long foundItemId);

    @Query("SELECT m FROM MatchRecord m WHERE (m.lostItem.reporter.id = :userId OR m.foundItem.reporter.id = :userId) AND m.status = 'ACTIVE' ORDER BY m.similarityScore DESC")
    List<MatchRecord> findActiveMatchesForUser(@Param("userId") Long userId);

    @Query("SELECT m FROM MatchRecord m WHERE (m.lostItem.id = :itemId OR m.foundItem.id = :itemId) AND m.status = 'ACTIVE' ORDER BY m.similarityScore DESC")
    List<MatchRecord> findActiveMatchesForItem(@Param("itemId") Long itemId);

    List<MatchRecord> findByStatusOrderBySimilarityScoreDesc(String status);
}
