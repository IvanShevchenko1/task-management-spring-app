package org.shevchenko.taskmanagementspringapp.mapper;

import org.mapstruct.Mapper;
import org.shevchenko.taskmanagementspringapp.config.MapperConfig;
import org.shevchenko.taskmanagementspringapp.dto.user.UserRegistrationRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserResponseDto;
import org.shevchenko.taskmanagementspringapp.model.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);
}

