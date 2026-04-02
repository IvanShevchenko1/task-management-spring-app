package org.shevchenko.taskmanagementspringapp.service;

import java.io.IOException;
import java.util.List;
import org.shevchenko.taskmanagementspringapp.dto.attachment.AttachmentResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    AttachmentResponseDto upload(Long taskId, MultipartFile file) throws IOException;

    List<AttachmentResponseDto> getByTaskId(Long taskId);

    void delete(Long id);
}
