package org.shevchenko.taskmanagementspringapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.shevchenko.taskmanagementspringapp.dto.user.UserResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserUpdateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserUpdateRoleRequestDto;
import org.shevchenko.taskmanagementspringapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Endpoints for managing users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get authenticated user profile")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto getAuthenticatedUser() {
        return userService.getAuthenticatedUser();
    }

    @Operation(summary = "Update authenticated user profile")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PutMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto updateAuthenticatedUser(
            @RequestBody UserUpdateRequestDto request) {
        return userService.updateAuthenticatedUser(request);
    }

    @Operation(summary = "Update user role by id")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{id}/role")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto updateRole(@PathVariable Long id,
                                      @RequestBody UserUpdateRoleRequestDto request) {
        return userService.updateRole(id,request);
    }
}
