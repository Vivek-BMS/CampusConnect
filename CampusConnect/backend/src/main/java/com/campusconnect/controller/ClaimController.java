package com.campusconnect.controller;

import com.campusconnect.dto.ApiResponse;
import com.campusconnect.dto.ClaimRequest;
import com.campusconnect.dto.ClaimResponse;
import com.campusconnect.dto.ClaimStatusUpdateRequest;
import com.campusconnect.entity.ClaimStatus;
import com.campusconnect.entity.User;
import com.campusconnect.repository.UserRepository;
import com.campusconnect.security.UserPrincipal;
import com.campusconnect.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/claims")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<ClaimResponse>> submitClaim(
            @Valid @RequestBody ClaimRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ClaimResponse response = claimService.submitClaim(request, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Ownership claim submitted for review", response));
    }

    @GetMapping("/my-claims")
    public ResponseEntity<ApiResponse<List<ClaimResponse>>> getMyClaims(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<ClaimResponse> claims = claimService.getClaimsByClaimant(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("My claims", claims));
    }

    @GetMapping("/item/{itemId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECURITY_STAFF')")
    public ResponseEntity<ApiResponse<List<ClaimResponse>>> getClaimsByItem(@PathVariable Long itemId) {
        List<ClaimResponse> claims = claimService.getClaimsByItem(itemId, true);
        return ResponseEntity.ok(ApiResponse.ok("Item claims", claims));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECURITY_STAFF')")
    public ResponseEntity<ApiResponse<ClaimResponse>> updateClaimStatus(
            @PathVariable Long id,
            @Valid @RequestBody ClaimStatusUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User reviewer = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("Reviewer not found"));
        ClaimResponse response = claimService.updateClaimStatus(id, request, reviewer);
        return ResponseEntity.ok(ApiResponse.ok("Claim status updated", response));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECURITY_STAFF')")
    public ResponseEntity<ApiResponse<List<ClaimResponse>>> getAllClaims(
            @RequestParam(required = false) ClaimStatus status) {
        List<ClaimResponse> claims = claimService.getAllClaims(status);
        return ResponseEntity.ok(ApiResponse.ok("All claims", claims));
    }
}
