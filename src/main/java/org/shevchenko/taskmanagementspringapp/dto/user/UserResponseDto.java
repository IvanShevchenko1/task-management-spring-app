package org.shevchenko.taskmanagementspringapp.dto.user;

public record UserResponseDto(
        Long id,
        String email,
        String username,
        String firstName,
        String lastName
) {
}
