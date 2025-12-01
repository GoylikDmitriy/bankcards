package com.goylik.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request for user authentication")
public record LoginRequest(
        @Schema(description = "User email", example = "user@mail.com")
        @Email @NotBlank
        String email,

        @Schema(description = "User password", example = "123456")
        @NotBlank
        String password
) {}

