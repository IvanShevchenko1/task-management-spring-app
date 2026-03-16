package org.shevchenko.taskmanagementspringapp.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.shevchenko.taskmanagementspringapp.config.MapperConfig;
import org.shevchenko.taskmanagementspringapp.dto.project.ProjectCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.project.ProjectResponseDto;
import org.shevchenko.taskmanagementspringapp.model.Project;

@Mapper(config = MapperConfig.class, uses = {UserMapper.class})
public interface ProjectMapper {
    Project toModel(ProjectCreateRequestDto request);

    @Mappings({
            @Mapping(target = "owner_id",
            source = "owner",
            qualifiedByName = "getIdFromUser")
    })
    ProjectResponseDto toDto(Project project);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ProjectCreateRequestDto request,@MappingTarget Project project);
}
