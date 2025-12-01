package com.goylik.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Request for performing an internal transfer between cards")
public record InternalTransferRequest(
        @Schema(description = "ID of the card from which money will be transferred", example = "10")
        @NotNull
        Long fromCardId,

        @Schema(description = "ID of the recipient card", example = "15")
        @NotNull
        Long toCardId,

        @Schema(description = "Amount to transfer", example = "150.00")
        @NotNull @Positive @DecimalMin(value = "1.00")
        BigDecimal amount
) {}

