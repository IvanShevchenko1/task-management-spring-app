package org.shevchenko.taskmanagementspringapp.dto.user;

public record UserUpdateRequestDto(
        String email,
        String username,
        String firstName,
        String lastName
) {
}
