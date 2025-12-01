package com.goylik.bankcards.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.YearMonth;

@Schema(description = "Bank card details")
public record CardResponse(
        @Schema(description = "Card ID", example = "5")
        Long id,

        @Schema(description = "Masked card number", example = "**** **** **** 1234")
        String maskedCardNumber,

        @Schema(description = "Owner name", example = "John Doe")
        String ownerName,

        @Schema(description = "Card status (ACTIVE, BLOCKED, EXPIRED)", example = "ACTIVE")
        String status,

        @Schema(description = "Expiry date in MM/yyyy format", example = "11/2027")
        @JsonFormat(pattern = "MM/yyyy")
        YearMonth expiryDate,

        @Schema(description = "Card balance", example = "250.00")
        BigDecimal balance
) {}

