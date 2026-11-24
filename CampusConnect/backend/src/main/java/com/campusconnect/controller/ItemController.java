package com.campusconnect.controller;

import com.campusconnect.dto.ApiResponse;
import com.campusconnect.dto.ItemRequest;
import com.campusconnect.dto.ItemResponse;
import com.campusconnect.entity.ItemStatus;
import com.campusconnect.entity.ItemType;
import com.campusconnect.security.UserPrincipal;
import com.campusconnect.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping("/report-lost")
    public ResponseEntity<ApiResponse<ItemResponse>> reportLostItem(
            @Valid @RequestBody ItemRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        request.setItemType(ItemType.LOST);
        ItemResponse response = itemService.reportItem(request, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Lost item reported successfully", response));
    }

    @PostMapping("/report-found")
    public ResponseEntity<ApiResponse<ItemResponse>> reportFoundItem(
            @Valid @RequestBody ItemRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        request.setItemType(ItemType.FOUND);
        ItemResponse response = itemService.reportItem(request, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Found item reported successfully", response));
    }

    @GetMapping("/browse")
    public ResponseEntity<ApiResponse<List<ItemResponse>>> browseItems(
            @RequestParam(required = false) ItemType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) ItemStatus status,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String search) {
        List<ItemResponse> items = itemService.searchItems(type, category, status, location, startDate, endDate, search);
        return ResponseEntity.ok(ApiResponse.ok("Items retrieved successfully", items));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemResponse>> getItemById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal != null ? userPrincipal.getId() : null;
        boolean isStaff = userPrincipal != null && (
                userPrincipal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SECURITY_STAFF"))
        );
        ItemResponse item = itemService.getItemById(id, userId, isStaff);
        return ResponseEntity.ok(ApiResponse.ok("Item details", item));
    }

    @GetMapping("/my-reports")
    public ResponseEntity<ApiResponse<List<ItemResponse>>> getMyReports(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<ItemResponse> myItems = itemService.getItemsByReporter(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("User reports", myItems));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        List<String> categories = Arrays.asList(
                "Electronics", "ID Cards", "Books", "Bags", "Wallets", "Accessories", "Clothing", "Other"
        );
        return ResponseEntity.ok(ApiResponse.ok("Categories", categories));
    }
}
