package com.goylik.bankcards.controller;

import com.goylik.bankcards.dto.request.ChangeUserRoleRequest;
import com.goylik.bankcards.dto.response.UserResponse;
import com.goylik.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Validated
@Tag(name = "Admin Users", description = "Administration endpoints for managing user accounts")
public class AdminUserController {
    private final UserService userService;

    @Operation(
            summary = "Change user role",
            description = "Updates the role of an existing user. Only accessible to administrators."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User role successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping
    public UserResponse changeUserRole(@Valid @RequestBody ChangeUserRoleRequest request) {
        return userService.updateRole(request);
    }

    @Operation(
            summary = "Get all users",
            description = "Returns a paginated list of all users in the system."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of users returned"),
    })
    @GetMapping
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @Operation(
            summary = "Get user by ID",
            description = "Returns user details by their unique identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User data returned"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable @Min(1) Long id) {
        return userService.getUserById(id);
    }

    @Operation(
            summary = "Soft delete a user",
            description = "Marks a user as deleted. The user is not removed from the database physically."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User soft-deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @Min(1) Long id) {
        userService.deleteUser(id);
    }
}
