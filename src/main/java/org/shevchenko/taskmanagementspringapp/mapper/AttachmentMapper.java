package org.shevchenko.taskmanagementspringapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.shevchenko.taskmanagementspringapp.config.MapperConfig;
import org.shevchenko.taskmanagementspringapp.dto.attachment.AttachmentResponseDto;
import org.shevchenko.taskmanagementspringapp.model.Attachment;

@Mapper(config = MapperConfig.class)
public interface AttachmentMapper {
    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "downloadUrl", ignore = true)
    AttachmentResponseDto toDto(Attachment attachment);
}
