package com.campusconnect.controller;

import com.campusconnect.dto.ApiResponse;
import com.campusconnect.dto.HandoverRequest;
import com.campusconnect.dto.HandoverResponse;
import com.campusconnect.entity.User;
import com.campusconnect.repository.UserRepository;
import com.campusconnect.security.UserPrincipal;
import com.campusconnect.service.HandoverService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/handovers")
public class HandoverController {

    @Autowired
    private HandoverService handoverService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECURITY_STAFF')")
    public ResponseEntity<ApiResponse<HandoverResponse>> recordHandover(
            @Valid @RequestBody HandoverRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User staff = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("Staff user not found"));
        HandoverResponse response = handoverService.recordHandover(request, staff);
        return ResponseEntity.ok(ApiResponse.ok("Handover logged and item status set to RECOVERED", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECURITY_STAFF')")
    public ResponseEntity<ApiResponse<List<HandoverResponse>>> getAllHandovers() {
        List<HandoverResponse> list = handoverService.getAllHandovers();
        return ResponseEntity.ok(ApiResponse.ok("Handover history", list));
    }

    @GetMapping("/my-handovers")
    public ResponseEntity<ApiResponse<List<HandoverResponse>>> getMyHandovers(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<HandoverResponse> list = handoverService.getHandoversForUser(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("My recovery handovers", list));
    }
}
