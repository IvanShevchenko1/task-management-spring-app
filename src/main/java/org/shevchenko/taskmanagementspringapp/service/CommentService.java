package org.shevchenko.taskmanagementspringapp.service;

import org.shevchenko.taskmanagementspringapp.dto.comment.CommentCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.comment.CommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    CommentResponseDto addComment(CommentCreateRequestDto requestDto);

    Page<CommentResponseDto> getCommentsByTaskId(Long taskId, Pageable pageable);

    void deleteComment(Long id);
}
