package org.shevchenko.taskmanagementspringapp.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shevchenko.taskmanagementspringapp.dto.user.UserRegistrationRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserUpdateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserUpdateRoleRequestDto;
import org.shevchenko.taskmanagementspringapp.exception.EntityNotFoundException;
import org.shevchenko.taskmanagementspringapp.exception.RegistrationException;
import org.shevchenko.taskmanagementspringapp.mapper.UserMapper;
import org.shevchenko.taskmanagementspringapp.model.Role;
import org.shevchenko.taskmanagementspringapp.model.User;
import org.shevchenko.taskmanagementspringapp.repository.RoleRepository;
import org.shevchenko.taskmanagementspringapp.repository.UserRepository;
import org.shevchenko.taskmanagementspringapp.util.TestDataFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void register_shouldEncodePasswordAttachDefaultRoleAndSave() {
        UserRegistrationRequestDto requestDto = TestDataFactory.userRegistrationRequest();
        User mappedUser = TestDataFactory.user(null);
        Role userRole = TestDataFactory.role(Role.RoleName.USER);
        UserResponseDto responseDto = TestDataFactory.userResponse(1L);

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(mappedUser);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encoded-secret");
        when(roleRepository.findByRole(Role.RoleName.USER)).thenReturn(Optional.of(userRole));
        when(userMapper.toDto(mappedUser)).thenReturn(responseDto);

        UserResponseDto actual = userService.register(requestDto);

        assertThat(actual).isEqualTo(responseDto);
        assertThat(mappedUser.getPassword()).isEqualTo("encoded-secret");
        assertThat(mappedUser.getRoles()).contains(userRole);
        verify(userRepository).save(mappedUser);
    }

    @Test
    void register_shouldRejectDuplicateEmail() {
        UserRegistrationRequestDto requestDto = TestDataFactory.userRegistrationRequest();
        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.register(requestDto))
                .isInstanceOf(RegistrationException.class)
                .hasMessage("Provided Email is already registered: john@example.com");
    }

    @Test
    void register_shouldFailWhenDefaultRoleMissing() {
        UserRegistrationRequestDto requestDto = TestDataFactory.userRegistrationRequest();
        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(TestDataFactory.user(null));
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encoded-secret");
        when(roleRepository.findByRole(Role.RoleName.USER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.register(requestDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Default role USER is missing. Add roles first.");
    }

    @Test
    void getAuthenticatedUser_shouldReturnMappedPrincipal() {
        User authenticatedUser = TestDataFactory.standardUser(7L);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        authenticatedUser,
                        null,
                        authenticatedUser.getAuthorities()
                )
        );
        UserResponseDto responseDto = TestDataFactory.userResponse(7L);
        when(userMapper.toDto(authenticatedUser)).thenReturn(responseDto);

        assertThat(userService.getAuthenticatedUser()).isEqualTo(responseDto);
    }

    @Test
    void updateAuthenticatedUser_shouldMapAndSavePrincipal() {
        User authenticatedUser = TestDataFactory.standardUser(7L);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        authenticatedUser,
                        null,
                        authenticatedUser.getAuthorities()
                )
        );
        UserUpdateRequestDto requestDto = TestDataFactory.userUpdateRequest();
        UserResponseDto responseDto = TestDataFactory.userResponse(7L);

        when(userRepository.save(authenticatedUser)).thenReturn(authenticatedUser);
        when(userMapper.toDto(authenticatedUser)).thenReturn(responseDto);

        UserResponseDto actual = userService.updateAuthenticatedUser(requestDto);

        assertThat(actual).isEqualTo(responseDto);
        verify(userMapper).updateEntity(requestDto, authenticatedUser);
    }

    @Test
    void updateRole_shouldReplaceUsersRoles() {
        User user = TestDataFactory.standardUser(5L);
        Role adminRole = TestDataFactory.role(Role.RoleName.ADMIN);
        UserResponseDto responseDto = TestDataFactory.userResponse(5L);

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(roleRepository.findByRole(Role.RoleName.ADMIN)).thenReturn(Optional.of(adminRole));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(responseDto);

        UserResponseDto actual = userService.updateRole(5L, new UserUpdateRoleRequestDto("admin"));

        assertThat(actual).isEqualTo(responseDto);
        assertThat(user.getRoles()).containsExactly(adminRole);
    }

    @Test
    void updateRole_shouldRejectUnknownRoleValue() {
        User user = TestDataFactory.standardUser(5L);
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.updateRole(5L, new UserUpdateRoleRequestDto("manager")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid role: manager");
    }

    @Test
    void updateRole_shouldThrowWhenRoleEntityMissing() {
        User user = TestDataFactory.standardUser(5L);
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(roleRepository.findByRole(Role.RoleName.ADMIN)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateRole(5L, new UserUpdateRoleRequestDto("ADMIN")))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Can't find role: ADMIN");
    }

    @Test
    void getAuthenticatedUserOrThrow_shouldRejectMissingAuthentication() {
        assertThatThrownBy(() -> userService.getAuthenticatedUserOrThrow())
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("User is not authenticated");
    }

    @Test
    void getAuthenticatedUserOrThrow_shouldRejectInvalidPrincipal() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "plain-string",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );

        assertThatThrownBy(() -> userService.getAuthenticatedUserOrThrow())
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Authenticated principal is invalid");
    }

    @Test
    void getAuthenticatedUserOrThrow_shouldReturnUserPrincipal() {
        User authenticatedUser = TestDataFactory.standardUser(7L);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        authenticatedUser,
                        null,
                        authenticatedUser.getAuthorities()
                )
        );

        assertThat(userService.getAuthenticatedUserOrThrow()).isSameAs(authenticatedUser);
    }
}
