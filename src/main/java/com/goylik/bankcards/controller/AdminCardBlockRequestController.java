package com.goylik.bankcards.controller;

import com.goylik.bankcards.dto.request.CardBlockRequestFilter;
import com.goylik.bankcards.dto.response.CardBlockRequestResponse;
import com.goylik.bankcards.service.CardBlockRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/block-requests")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Card Block Requests", description = "Endpoints for managing card block requests by admin")
public class AdminCardBlockRequestController {
    private final CardBlockRequestService cardBlockRequestService;

    @Operation(
            summary = "Get pending card block requests",
            description = "Returns a paginated list of card block requests that are pending approval"
    )
    @GetMapping("/pending")
    public Page<CardBlockRequestResponse> getPendingCardBlockRequests(
            CardBlockRequestFilter filter, Pageable pageable) {
        return cardBlockRequestService.getPendingRequests(filter, pageable);
    }

    @Operation(
            summary = "Approve a card block request",
            description = "Approve a specific card block request by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request approved successfully"),
            @ApiResponse(responseCode = "400", description = "Card is already blocked or expired or request is already processed."),
            @ApiResponse(responseCode = "404", description = "Card block request not found")
    })
    @PutMapping("/{requestId}/approve")
    public void approveCardBlockRequest(@PathVariable @Min(1) Long requestId) {
        cardBlockRequestService.approveRequest(requestId);
    }

    @Operation(
            summary = "Reject a card block request",
            description = "Reject a specific card block request by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request rejected successfully"),
            @ApiResponse(responseCode = "400", description = "Request is already processed"),
            @ApiResponse(responseCode = "404", description = "Card block request not found")
    })
    @PutMapping("/{requestId}/reject")
    public void rejectCardBlockRequest(@PathVariable @Min(1) Long requestId) {
        cardBlockRequestService.rejectRequest(requestId);
    }
}
