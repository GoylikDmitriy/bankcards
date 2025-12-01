package com.goylik.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request dto for updating a user's role")
public record ChangeUserRoleRequest(

        @Schema(description = "User ID whose role is being updated", example = "12")
        @NotNull Long userId,

        @Schema(description = "New role to assign to the user", example = "ADMIN")
        @NotBlank String role
) {
}
