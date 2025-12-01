package com.goylik.bankcards.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User information")
public record UserResponse(
        @Schema(description = "User ID", example = "1")
        Long id,

        @Schema(description = "User email", example = "user@mail.com")
        String email,

        @Schema(description = "User role", example = "USER")
        String role
) {}

