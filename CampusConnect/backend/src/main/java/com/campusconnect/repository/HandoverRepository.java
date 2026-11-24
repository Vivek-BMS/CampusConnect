package com.campusconnect.repository;

import com.campusconnect.entity.Handover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HandoverRepository extends JpaRepository<Handover, Long> {
    Optional<Handover> findByItemId(Long itemId);
    Optional<Handover> findByClaimId(Long claimId);
    List<Handover> findByRecipientIdOrderByHandoverDateDesc(Long recipientId);
    List<Handover> findAllByOrderByHandoverDateDesc();
}
