package org.shevchenko.taskmanagementspringapp.service;

import java.io.IOException;
import org.shevchenko.taskmanagementspringapp.dto.attachment.AttachmentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    AttachmentResponseDto upload(Long taskId, MultipartFile file) throws IOException;

    Page<AttachmentResponseDto> getByTaskId(Long taskId, Pageable pageable);

    void delete(Long id);
}
