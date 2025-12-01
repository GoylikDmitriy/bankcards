package com.goylik.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Filter for searching user cards")
public record CardFilter (
        @Schema(
                description = "Card status (ACTIVE, BLOCKED, EXPIRED)",
                example = "ACTIVE"
        )
        String status,

        @Schema(
                description = "User ID to filter cards by user",
                example = "1"
        )
        Long userId
) {
}
