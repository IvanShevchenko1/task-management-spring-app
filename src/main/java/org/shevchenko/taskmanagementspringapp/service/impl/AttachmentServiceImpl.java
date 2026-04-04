package org.shevchenko.taskmanagementspringapp.service.impl;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.shevchenko.taskmanagementspringapp.dto.attachment.AttachmentResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.dropbox.DropboxStoredFile;
import org.shevchenko.taskmanagementspringapp.exception.EntityNotFoundException;
import org.shevchenko.taskmanagementspringapp.exception.ForbiddenOperationException;
import org.shevchenko.taskmanagementspringapp.mapper.AttachmentMapper;
import org.shevchenko.taskmanagementspringapp.model.Attachment;
import org.shevchenko.taskmanagementspringapp.model.Task;
import org.shevchenko.taskmanagementspringapp.model.User;
import org.shevchenko.taskmanagementspringapp.repository.AttachmentRepository;
import org.shevchenko.taskmanagementspringapp.repository.TaskRepository;
import org.shevchenko.taskmanagementspringapp.service.AttachmentService;
import org.shevchenko.taskmanagementspringapp.service.DropboxStorageService;
import org.shevchenko.taskmanagementspringapp.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final AttachmentMapper attachmentMapper;
    private final DropboxStorageService dropboxStorageService;
    private final UserService userService;

    @Override
    public AttachmentResponseDto upload(Long taskId, MultipartFile file) throws IOException {
        Task task = getAuthorizedTask(taskId);

        DropboxStoredFile storedFile =
                dropboxStorageService.upload(
                        taskId,
                        file.getOriginalFilename(),
                        file.getInputStream());

        Attachment attachment = new Attachment();
        attachment.setTask(task);
        attachment.setDropboxFileId(storedFile.fileId());
        attachment.setDropboxPath(storedFile.path());
        attachment.setFilename(storedFile.filename());

        Attachment saved = attachmentRepository.save(attachment);
        AttachmentResponseDto dto = attachmentMapper.toDto(saved);
        return new AttachmentResponseDto(
                dto.id(),
                dto.taskId(),
                dto.dropboxFileId(),
                dto.filename(),
                dto.uploadDate(),
                dropboxStorageService.createTemporaryDownloadLink(saved.getDropboxPath())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttachmentResponseDto> getByTaskId(Long taskId, Pageable pageable) {
        getAuthorizedTask(taskId);

        return attachmentRepository
                .findAllByTaskIdOrderByUploadDateDesc(taskId, pageable)
                .map(attachment -> new AttachmentResponseDto(
                        attachment.getId(),
                        attachment.getTask().getId(),
                        attachment.getDropboxFileId(),
                        attachment.getFilename(),
                        attachment.getUploadDate(),
                        dropboxStorageService.createTemporaryDownloadLink(
                                attachment.getDropboxPath())
                ));
    }

    @Override
    public void delete(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find attachment by id: "
                                + id));

        ensureTaskAccess(attachment.getTask());

        dropboxStorageService.delete(attachment.getDropboxPath());
        attachmentRepository.delete(attachment);
    }

    private Task getAuthorizedTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find task by id: " + taskId));
        ensureTaskAccess(task);
        return task;
    }

    private void ensureTaskAccess(Task task) {
        User user = userService.getAuthenticatedUserOrThrow();

        boolean isOwner = task.getProject().getOwner().getId().equals(user.getId());
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new ForbiddenOperationException(
                    "You are not allowed to access attachments for this task"
            );
        }
    }
}
