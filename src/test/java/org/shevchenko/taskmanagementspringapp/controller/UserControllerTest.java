package org.shevchenko.taskmanagementspringapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shevchenko.taskmanagementspringapp.dto.user.UserResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserUpdateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserUpdateRoleRequestDto;
import org.shevchenko.taskmanagementspringapp.service.UserService;
import org.shevchenko.taskmanagementspringapp.support.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    @Test
    void getAuthenticatedUser_shouldDelegateToService() {
        UserResponseDto responseDto = TestDataFactory.userResponse(7L);
        when(userService.getAuthenticatedUser()).thenReturn(responseDto);

        assertThat(userController.getAuthenticatedUser()).isEqualTo(responseDto);
    }

    @Test
    void updateAuthenticatedUser_shouldDelegateToService() {
        UserUpdateRequestDto requestDto = TestDataFactory.userUpdateRequest();
        UserResponseDto responseDto = TestDataFactory.userResponse(7L);
        when(userService.updateAuthenticatedUser(requestDto)).thenReturn(responseDto);

        assertThat(userController.updateAuthenticatedUser(requestDto)).isEqualTo(responseDto);
    }

    @Test
    void updateRole_shouldDelegateToService() {
        UserUpdateRoleRequestDto requestDto = TestDataFactory.userUpdateRoleRequest("ADMIN");
        UserResponseDto responseDto = TestDataFactory.userResponse(7L);
        when(userService.updateRole(7L, requestDto)).thenReturn(responseDto);

        assertThat(userController.updateRole(7L, requestDto)).isEqualTo(responseDto);
        verify(userService).updateRole(7L, requestDto);
    }
}
