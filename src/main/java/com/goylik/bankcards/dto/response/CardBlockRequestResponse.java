package com.goylik.bankcards.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response for a card block request")
public record CardBlockRequestResponse(
        @Schema(description = "Request ID", example = "10")
        Long id,

        @Schema(description = "Status of the request (PENDING, APPROVED, REJECTED)", example = "PENDING")
        String status,

        @Schema(description = "Timestamp when the request was created")
        LocalDateTime createdAt
) {}

