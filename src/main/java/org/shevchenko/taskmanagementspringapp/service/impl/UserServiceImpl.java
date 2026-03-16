package org.shevchenko.taskmanagementspringapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.shevchenko.taskmanagementspringapp.dto.user.UserRegistrationRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserResponseDto;
import org.shevchenko.taskmanagementspringapp.exception.RegistrationException;
import org.shevchenko.taskmanagementspringapp.mapper.UserMapper;
import org.shevchenko.taskmanagementspringapp.model.Role;
import org.shevchenko.taskmanagementspringapp.model.User;
import org.shevchenko.taskmanagementspringapp.repository.RoleRepository;
import org.shevchenko.taskmanagementspringapp.repository.UserRepository;
import org.shevchenko.taskmanagementspringapp.service.UserService;
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
}
