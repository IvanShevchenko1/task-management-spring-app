package org.shevchenko.taskmanagementspringapp.service.impl;

import lombok.RequiredArgsConstructor;
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
import org.shevchenko.taskmanagementspringapp.service.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException(
                    "Provided Email is already registered: " + requestDto.getEmail());
        }

        User user = userMapper.toModel(requestDto);

        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        Role userRole = roleRepository.findByRole(Role.RoleName.USER)
                .orElseThrow(() -> new IllegalStateException(
                        "Default role USER is missing. Add roles first."));
        user.getRoles().add(userRole);

        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto getAuthenticatedUser() {
        User user = getAuthenticatedUserOrThrow();
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateAuthenticatedUser(UserUpdateRequestDto request) {
        User user = getAuthenticatedUserOrThrow();
        userMapper.updateEntity(request, user);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDto updateRole(Long id, UserUpdateRoleRequestDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find user by id: " + id));

        Role.RoleName roleName;
        try {
            roleName = Role.RoleName.valueOf(request.role().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new RuntimeException("Invalid role: " + request.role());
        }

        Role role = roleRepository.findByRole(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Can't find role: " + roleName));

        user.getRoles().clear();
        user.getRoles().add(role);

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public User getAuthenticatedUserOrThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User user)) {
            throw new AccessDeniedException("Authenticated principal is invalid");
        }

        return user;
    }
}
