package com.goylik.bankcards.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response containing JWT and user details")
public record AuthResponse(
        @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String jwt,

        @Schema(description = "User ID", example = "1")
        Long id,

        @Schema(description = "User email", example = "user@mail.com")
        String email,

        @Schema(description = "User role", example = "ADMIN")
        String role
) {}

