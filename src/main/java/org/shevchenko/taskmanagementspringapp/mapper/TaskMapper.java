package org.shevchenko.taskmanagementspringapp.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.shevchenko.taskmanagementspringapp.config.MapperConfig;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskUpdateRequestDto;
import org.shevchenko.taskmanagementspringapp.model.Task;

@Mapper(config = MapperConfig.class)
public interface TaskMapper {
    @Mapping(target = "project", ignore = true)
    Task toModel(TaskCreateRequestDto dto);

    @Mapping(target = "projectId", source = "project.id")
    TaskResponseDto toDto(Task task);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TaskUpdateRequestDto dto, @MappingTarget Task task);
}
