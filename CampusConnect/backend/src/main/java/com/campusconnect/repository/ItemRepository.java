package com.campusconnect.repository;

import com.campusconnect.entity.Item;
import com.campusconnect.entity.ItemStatus;
import com.campusconnect.entity.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByItemTypeOrderByIncidentDateDesc(ItemType itemType);

    List<Item> findByReporterIdOrderByCreatedAtDesc(Long userId);

    List<Item> findByItemTypeAndStatusOrderByIncidentDateDesc(ItemType itemType, ItemStatus status);

    long countByItemType(ItemType itemType);

    long countByStatus(ItemStatus status);

    @Query("SELECT i FROM Item i WHERE " +
           "(:itemType IS NULL OR i.itemType = :itemType) AND " +
           "(:category IS NULL OR :category = '' OR LOWER(i.category) = LOWER(:category)) AND " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:location IS NULL OR :location = '' OR LOWER(i.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:startDate IS NULL OR i.incidentDate >= :startDate) AND " +
           "(:endDate IS NULL OR i.incidentDate <= :endDate) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           " LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(i.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(i.color) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY i.incidentDate DESC")
    List<Item> searchAndFilter(
            @Param("itemType") ItemType itemType,
            @Param("category") String category,
            @Param("status") ItemStatus status,
            @Param("location") String location,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("keyword") String keyword
    );

    @Query("SELECT i FROM Item i WHERE i.itemType = :itemType AND i.status != 'RECOVERED' AND i.status != 'CLOSED'")
    List<Item> findActiveItemsByType(@Param("itemType") ItemType itemType);
}
