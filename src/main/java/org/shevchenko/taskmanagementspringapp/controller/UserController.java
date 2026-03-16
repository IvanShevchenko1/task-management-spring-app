package org.shevchenko.taskmanagementspringapp.controller;

import lombok.RequiredArgsConstructor;
import org.shevchenko.taskmanagementspringapp.dto.user.UserResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserUpdateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserUpdateRoleRequestDto;
import org.shevchenko.taskmanagementspringapp.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/me")
    public UserResponseDto getAuthenticatedUser() {
        return userService.getAuthenticatedUser();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PutMapping("/me")
    public UserResponseDto updateAuthenticatedUser(
            @RequestBody UserUpdateRequestDto request) {
        return userService.updateAuthenticatedUser(request);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{id}/role")
    public UserResponseDto updateRole(@PathVariable Long id,
                                      @RequestBody UserUpdateRoleRequestDto request) {
        return userService.updateRole(id,request);
    }
}
