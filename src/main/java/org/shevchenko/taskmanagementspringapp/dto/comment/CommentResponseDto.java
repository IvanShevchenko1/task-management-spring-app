package org.shevchenko.taskmanagementspringapp.dto.comment;

import java.time.LocalDateTime;

public record CommentResponseDto(
        Long id,
        Long taskId,
        Long userId,
        String text,
        LocalDateTime timestamp
) {
}
