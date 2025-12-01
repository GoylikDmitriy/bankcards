package com.goylik.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Filter for searching request to block a card")
public record CardBlockRequestFilter(

        @Schema(
                description = "Status of request (PENDING, APPROVED, REJECTED)",
                example = "BLOCKED"
        )
        String status,

        @Schema(
                description = "User ID to filter requests by user",
                example = "123"
        )
        Long userId
) {
}
