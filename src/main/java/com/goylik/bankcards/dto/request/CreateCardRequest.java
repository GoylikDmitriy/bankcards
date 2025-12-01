package com.goylik.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Request for creating a new bank card")
public record CreateCardRequest(
        @Schema(description = "Full name of the card owner", example = "John Doe")
        @NotBlank @Size(max = 100)
        String ownerName,

        @Schema(description = "Validity period of the card in years", example = "3")
        @Min(1) @Max(5)
        Integer validityYears,

        @Schema(description = "ID of the user who will own the card", example = "123")
        @NotNull
        Long userId
) {}

