package org.shevchenko.taskmanagementspringapp.service;

import java.util.List;
import org.shevchenko.taskmanagementspringapp.dto.comment.CommentCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.comment.CommentResponseDto;

public interface CommentService {
    CommentResponseDto addComment(CommentCreateRequestDto requestDto);

    List<CommentResponseDto> getCommentsByTaskId(Long taskId);

    void deleteComment(Long id);
}
