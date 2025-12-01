package com.goylik.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request for creating a new user account")
public record SignupRequest(
        @Schema(description = "User email", example = "user@mail.com")
        @Email @NotBlank @Size(max = 100)
        String email,

        @Schema(description = "User password", example = "123456")
        @NotBlank @Size(min = 6, max = 100)
        String password
) {}

