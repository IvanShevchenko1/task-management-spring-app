package org.shevchenko.taskmanagementspringapp.controller;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.shevchenko.taskmanagementspringapp.dto.attachment.AttachmentResponseDto;
import org.shevchenko.taskmanagementspringapp.service.AttachmentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AttachmentResponseDto upload(
            @RequestParam Long taskId,
            @RequestParam MultipartFile file
    ) throws IOException {
        return attachmentService.upload(taskId, file);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AttachmentResponseDto> getByTaskId(@RequestParam Long taskId) {
        return attachmentService.getByTaskId(taskId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        attachmentService.delete(id);
    }
}
