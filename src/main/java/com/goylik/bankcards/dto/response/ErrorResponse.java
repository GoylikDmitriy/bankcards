package com.goylik.bankcards.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
@Schema(description = "Error response containing message and validation errors")
public class ErrorResponse {

    @Schema(description = "Error message", example = "Validation failed")
    private String message;

    @Schema(description = "Field-specific validation errors", example = "fieldName: message")
    private Map<String, String> errors;

    public ErrorResponse(String message) {
        this.message = message;
        this.errors = null;
    }
}

