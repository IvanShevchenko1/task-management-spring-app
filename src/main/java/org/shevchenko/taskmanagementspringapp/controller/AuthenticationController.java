package org.shevchenko.taskmanagementspringapp.controller;

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
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto register(
            @RequestBody @Valid UserRegistrationRequestDto request)
            throws RegistrationException {
        return userService.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }

}
