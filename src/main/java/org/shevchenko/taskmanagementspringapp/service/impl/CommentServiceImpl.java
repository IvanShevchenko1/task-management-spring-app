package org.shevchenko.taskmanagementspringapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.shevchenko.taskmanagementspringapp.dto.comment.CommentCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.comment.CommentResponseDto;
import org.shevchenko.taskmanagementspringapp.exception.EntityNotFoundException;
import org.shevchenko.taskmanagementspringapp.exception.ForbiddenOperationException;
import org.shevchenko.taskmanagementspringapp.mapper.CommentMapper;
import org.shevchenko.taskmanagementspringapp.model.Comment;
import org.shevchenko.taskmanagementspringapp.model.Task;
import org.shevchenko.taskmanagementspringapp.model.User;
import org.shevchenko.taskmanagementspringapp.repository.CommentRepository;
import org.shevchenko.taskmanagementspringapp.repository.TaskRepository;
import org.shevchenko.taskmanagementspringapp.service.CommentService;
import org.shevchenko.taskmanagementspringapp.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final CommentMapper commentMapper;
    private final UserService userService;

    @Override
    public CommentResponseDto addComment(CommentCreateRequestDto requestDto) {
        Task task = taskRepository.findById(requestDto.taskId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find task by id: " + requestDto.taskId()
                ));

        User user = userService.getAuthenticatedUserOrThrow();

        Comment comment = commentMapper.toModel(requestDto);
        comment.setTask(task);
        comment.setUser(user);

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponseDto> getCommentsByTaskId(Long taskId,
                                                        Pageable pageable) {
        taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find task by id: " + taskId
                ));

        return commentRepository.findAllByTaskIdOrderByTimestampAsc(taskId, pageable)
                .map(commentMapper::toDto);
    }

    @Override
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find comment by id: " + id
                ));

        User user = userService.getAuthenticatedUserOrThrow();

        boolean isOwner = comment.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new ForbiddenOperationException("You are not allowed to delete this comment");
        }

        commentRepository.delete(comment);
    }
}
