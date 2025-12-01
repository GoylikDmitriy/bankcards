package com.goylik.bankcards.controller;

import com.goylik.bankcards.dto.request.LoginRequest;
import com.goylik.bankcards.dto.request.SignupRequest;
import com.goylik.bankcards.dto.response.AuthResponse;
import com.goylik.bankcards.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and authentication")
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "User signup",
            description = "Registers a new user with the provided signup information"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid signup request data"),
            @ApiResponse(responseCode = "409", description = "User with the given email already exists")
    })
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@Valid @RequestBody SignupRequest request) {
        authService.register(request);
    }

    @Operation(
            summary = "User signin",
            description = "Authenticates a user and returns a JWT token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid email or password")
    })
    @PostMapping("/signin")
    public AuthResponse signin(@Valid @RequestBody LoginRequest request) {
        return authService.authenticate(request);
    }
}
