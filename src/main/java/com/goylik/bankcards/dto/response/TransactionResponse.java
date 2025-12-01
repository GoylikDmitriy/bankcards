package com.goylik.bankcards.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Details of the completed transaction")
public record TransactionResponse(
        @Schema(description = "Transaction ID", example = "100")
        Long id,

        @Schema(description = "Masked sender card number", example = "**** **** **** 1111")
        String maskedCardNumberFrom,

        @Schema(description = "Masked receiver card number", example = "**** **** **** 2222")
        String maskedCardNumberTo,

        @Schema(description = "Transaction amount", example = "50.00")
        BigDecimal amount,

        @Schema(description = "Date and time when the transaction was created")
        LocalDateTime createdAt
) {}

