package org.shevchenko.taskmanagementspringapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.shevchenko.taskmanagementspringapp.dto.user.UserLoginRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserLoginResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserRegistrationRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserResponseDto;
import org.shevchenko.taskmanagementspringapp.exception.RegistrationException;
import org.shevchenko.taskmanagementspringapp.security.AuthenticationService;
import org.shevchenko.taskmanagementspringapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication and registration endpoints")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Register new user")
    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto register(
            @RequestBody @Valid UserRegistrationRequestDto request)
            throws RegistrationException {
        return userService.register(request);
    }

    @Operation(summary = "Register new user")
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }

}
