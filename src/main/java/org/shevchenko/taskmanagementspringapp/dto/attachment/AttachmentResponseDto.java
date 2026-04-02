package org.shevchenko.taskmanagementspringapp.dto.attachment;

import java.time.LocalDateTime;

public record AttachmentResponseDto(
        Long id,
        Long taskId,
        String dropboxFileId,
        String filename,
        LocalDateTime uploadDate,
        String downloadUrl
) {
}
