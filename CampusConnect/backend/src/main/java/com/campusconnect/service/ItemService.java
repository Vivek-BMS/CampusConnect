package com.campusconnect.service;

import com.campusconnect.dto.ItemRequest;
import com.campusconnect.dto.ItemResponse;
import com.campusconnect.entity.*;
import com.campusconnect.repository.ItemRepository;
import com.campusconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private SmartMatchingService smartMatchingService;

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private AuditLogService auditLogService;

    @Transactional
    public ItemResponse reportItem(ItemRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Item item = new Item();
        item.setReporter(user);
        item.setItemType(request.getItemType());
        item.setTitle(request.getTitle().trim());
        item.setCategory(request.getCategory().trim());
        item.setDescription(request.getDescription().trim());
        item.setColor(request.getColor() != null ? request.getColor().trim() : null);
        item.setBrand(request.getBrand() != null ? request.getBrand().trim() : null);
        item.setLocation(request.getLocation().trim());
        item.setIncidentDate(request.getIncidentDate());
        item.setIncidentTime(request.getIncidentTime());
        item.setImageUrl(request.getImageUrl());
        item.setStatus(ItemStatus.OPEN);
        item.setCustodyStatus(request.getCustodyStatus());
        item.setAdditionalDetails(request.getAdditionalDetails());

        Item savedItem = itemRepository.save(item);

        auditLogService.logAction(user, "REPORT_" + item.getItemType(), "Item", savedItem.getId(),
                "Reported " + item.getItemType() + " item: " + savedItem.getTitle(), null);

        // Run smart matching automatically on report creation!
        smartMatchingService.findAndRecordMatchesForItem(savedItem);

        ItemResponse response = ItemResponse.fromEntity(savedItem, true);
        if (savedItem.getItemType() == ItemType.FOUND) {
            response.setQrCodeData(qrCodeService.generateItemQrBase64(savedItem.getId(), savedItem.getTitle()));
        }
        return response;
    }

    @Transactional(readOnly = true)
    public ItemResponse getItemById(Long id, Long currentUserId, boolean isStaff) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));

        boolean canViewPrivate = isStaff || (currentUserId != null && item.getReporter().getId().equals(currentUserId));
        ItemResponse response = ItemResponse.fromEntity(item, canViewPrivate);

        if (item.getItemType() == ItemType.FOUND) {
            response.setQrCodeData(qrCodeService.generateItemQrBase64(item.getId(), item.getTitle()));
        }
        return response;
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> searchItems(ItemType itemType, String category, ItemStatus status,
                                          String location, LocalDate startDate, LocalDate endDate,
                                          String keyword) {
        List<Item> items = itemRepository.searchAndFilter(itemType, category, status, location, startDate, endDate, keyword);
        return items.stream()
                .map(i -> ItemResponse.fromEntity(i, false))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getItemsByReporter(Long userId) {
        return itemRepository.findByReporterIdOrderByCreatedAtDesc(userId).stream()
                .map(i -> ItemResponse.fromEntity(i, true))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getAllItems(ItemType type) {
        List<Item> items = (type != null) ?
                itemRepository.findByItemTypeOrderByIncidentDateDesc(type) :
                itemRepository.findAll();

        return items.stream()
                .map(i -> ItemResponse.fromEntity(i, true))
                .collect(Collectors.toList());
    }

    @Transactional
    public Item updateItemStatus(Long itemId, ItemStatus newStatus, User modifier) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setStatus(newStatus);
        Item updated = itemRepository.save(item);

        auditLogService.logAction(modifier, "UPDATE_STATUS", "Item", itemId,
                "Status updated to: " + newStatus, null);
        return updated;
    }
}
