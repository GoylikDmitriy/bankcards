package com.goylik.bankcards.controller;

import com.goylik.bankcards.dto.request.CardFilter;
import com.goylik.bankcards.dto.response.CardBlockRequestResponse;
import com.goylik.bankcards.dto.response.CardResponse;
import com.goylik.bankcards.service.CardBlockRequestService;
import com.goylik.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/cards")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Cards", description = "Operations for managing user bank cards")
public class UserCardController {
    private final CardService cardService;
    private final CardBlockRequestService cardBlockRequestService;

    @Operation(
            summary = "Get user cards",
            description = "Retrieves a paginated list of the current user's bank cards, optionally filtered"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cards retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CardResponse.class)))
    })
    @GetMapping
    public Page<CardResponse> getUserCards(CardFilter filter, Pageable pageable) {
        return cardService.getUserCards(filter, pageable);
    }

    @Operation(
            summary = "Get card details",
            description = "Retrieves detailed information for a specific user card"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Card details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CardResponse.class))),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @GetMapping("/{cardId}")
    public CardResponse getUserCardDetails(@PathVariable @Min(1) Long cardId) {
        return cardService.getUserCardById(cardId);
    }

    @Operation(
            summary = "Request card block",
            description = "Creates a request to block a specific user card"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Card block request created",
                    content = @Content(schema = @Schema(implementation = CardBlockRequestResponse.class))),
            @ApiResponse(responseCode = "400", description = "Card is already blocked or expired"),
            @ApiResponse(responseCode = "404", description = "Card not found"),
            @ApiResponse(responseCode = "409", description = "Request already exists")
    })
    @PostMapping("/{cardId}/request-block")
    @ResponseStatus(HttpStatus.CREATED)
    public CardBlockRequestResponse requestCardBlock(@PathVariable @Min(1) Long cardId) {
        return cardBlockRequestService.createRequest(cardId);
    }
}
