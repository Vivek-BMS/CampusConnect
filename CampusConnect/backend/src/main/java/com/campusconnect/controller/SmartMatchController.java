package com.campusconnect.controller;

import com.campusconnect.dto.ApiResponse;
import com.campusconnect.dto.MatchResponse;
import com.campusconnect.security.UserPrincipal;
import com.campusconnect.service.SmartMatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matches")
public class SmartMatchController {

    @Autowired
    private SmartMatchingService smartMatchingService;

    @GetMapping("/my-matches")
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getMyMatches(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<MatchResponse> matches = smartMatchingService.getMatchesForUser(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Potential matches", matches));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECURITY_STAFF')")
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getAllMatches() {
        List<MatchResponse> matches = smartMatchingService.getAllActiveMatches();
        return ResponseEntity.ok(ApiResponse.ok("All active smart matches", matches));
    }
}
