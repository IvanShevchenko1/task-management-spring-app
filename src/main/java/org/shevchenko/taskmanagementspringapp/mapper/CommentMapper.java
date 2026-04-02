package org.shevchenko.taskmanagementspringapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.shevchenko.taskmanagementspringapp.config.MapperConfig;
import org.shevchenko.taskmanagementspringapp.dto.comment.CommentCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.comment.CommentResponseDto;
import org.shevchenko.taskmanagementspringapp.model.Comment;

@Mapper(config = MapperConfig.class)
public interface CommentMapper {
    Comment toModel(CommentCreateRequestDto dto);

    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "userId", source = "user.id")
    CommentResponseDto toDto(Comment comment);
}
