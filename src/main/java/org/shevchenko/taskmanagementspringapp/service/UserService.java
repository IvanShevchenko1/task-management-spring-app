package org.shevchenko.taskmanagementspringapp.service;

import org.shevchenko.taskmanagementspringapp.dto.user.UserRegistrationRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserUpdateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserUpdateRoleRequestDto;
import org.shevchenko.taskmanagementspringapp.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    UserResponseDto getAuthenticatedUser();

    UserResponseDto updateAuthenticatedUser(UserUpdateRequestDto request);

    UserResponseDto updateRole(Long id, UserUpdateRoleRequestDto request);
}
