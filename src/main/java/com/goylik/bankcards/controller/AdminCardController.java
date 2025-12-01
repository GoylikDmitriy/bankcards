package com.goylik.bankcards.controller;

import com.goylik.bankcards.dto.request.CardFilter;
import com.goylik.bankcards.dto.request.CreateCardRequest;
import com.goylik.bankcards.dto.response.CardResponse;
import com.goylik.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/cards")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Cards", description = "Endpoints for managing bank cards by admin")
public class AdminCardController {
    private final CardService cardService;

    @Operation(
            summary = "Create a new card",
            description = "Creates a new bank card with the provided details"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Card created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CardResponse createCard(@Valid @RequestBody CreateCardRequest request) {
        return cardService.createCard(request);
    }

    @Operation(
            summary = "Get all cards",
            description = "Returns a paginated list of all cards with optional filtering"
    )
    @GetMapping
    public Page<CardResponse> getAllCards(CardFilter filter, Pageable pageable) {
        return cardService.getAllCards(filter, pageable);
    }

    @Operation(
            summary = "Block a card",
            description = "Blocks a card by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Card blocked successfully"),
            @ApiResponse(responseCode = "400", description = "Card is expired or already blocked."),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @PutMapping("/{cardId}/block")
    public void blockCard(@PathVariable @Min(1) Long cardId) {
        cardService.blockCard(cardId);
    }

    @Operation(
            summary = "Activate a card",
            description = "Activates a card by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Card activated successfully"),
            @ApiResponse(responseCode = "400", description = "Card is expired or already active."),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @PutMapping("/{cardId}/activate")
    public void activateCard(@PathVariable @Min(1) Long cardId) {
        cardService.activateCard(cardId);
    }

    @Operation(
            summary = "Soft delete a card",
            description = "Marks a card as deleted. The card is not removed from the database physically."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Card deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @DeleteMapping("/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable @Min(1) Long cardId) {
        cardService.deleteCard(cardId);
    }
}
