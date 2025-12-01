package com.goylik.bankcards.controller;

import com.goylik.bankcards.dto.request.InternalTransferRequest;
import com.goylik.bankcards.dto.response.TransactionResponse;
import com.goylik.bankcards.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Operations related to user transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Make internal transfer",
            description = "Transfers funds between two cards of the same user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transaction created successfully",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid transfer request data")
    })
    @PostMapping
    public TransactionResponse makeInternalTransfer(
            @Valid @RequestBody InternalTransferRequest request) {
        return transactionService.makeInternalTransfer(request);
    }
}
