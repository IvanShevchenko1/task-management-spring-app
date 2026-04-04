package org.shevchenko.taskmanagementspringapp.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shevchenko.taskmanagementspringapp.dto.comment.CommentCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.comment.CommentResponseDto;
import org.shevchenko.taskmanagementspringapp.exception.EntityNotFoundException;
import org.shevchenko.taskmanagementspringapp.mapper.CommentMapper;
import org.shevchenko.taskmanagementspringapp.model.Comment;
import org.shevchenko.taskmanagementspringapp.model.Project;
import org.shevchenko.taskmanagementspringapp.model.Task;
import org.shevchenko.taskmanagementspringapp.model.User;
import org.shevchenko.taskmanagementspringapp.repository.CommentRepository;
import org.shevchenko.taskmanagementspringapp.repository.TaskRepository;
import org.shevchenko.taskmanagementspringapp.service.UserService;
import org.shevchenko.taskmanagementspringapp.support.TestDataFactory;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private UserService userService;
    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void addComment_shouldBindTaskAndUserAndSave() {
        CommentCreateRequestDto requestDto = TestDataFactory.commentCreateRequest(10L);
        User user = TestDataFactory.standardUser(1L);
        Project project = TestDataFactory.project(2L, user);
        Task task = TestDataFactory.task(10L, project);
        Comment mappedComment = new Comment();
        Comment savedComment = TestDataFactory.comment(5L, task, user);
        CommentResponseDto responseDto = TestDataFactory.commentResponse(5L, 10L, 1L);

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(userService.getAuthenticatedUserOrThrow()).thenReturn(user);
        when(commentMapper.toModel(requestDto)).thenReturn(mappedComment);
        when(commentRepository.save(mappedComment)).thenReturn(savedComment);
        when(commentMapper.toDto(savedComment)).thenReturn(responseDto);

        CommentResponseDto actual = commentService.addComment(requestDto);

        assertThat(actual).isEqualTo(responseDto);
        assertThat(mappedComment.getTask()).isEqualTo(task);
        assertThat(mappedComment.getUser()).isEqualTo(user);
    }

    @Test
    void addComment_shouldThrowWhenTaskMissing() {
        when(taskRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.addComment(TestDataFactory.commentCreateRequest(10L)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Can't find task by id: 10");

        verify(commentRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void getCommentsByTaskId_shouldReturnMappedComments() {
        User user = TestDataFactory.standardUser(1L);
        Project project = TestDataFactory.project(2L, user);
        Task task = TestDataFactory.task(10L, project);
        Comment first = TestDataFactory.comment(1L, task, user);
        Comment second = TestDataFactory.comment(2L, task, user);
        CommentResponseDto firstDto = TestDataFactory.commentResponse(1L, 10L, 1L);
        CommentResponseDto secondDto = TestDataFactory.commentResponse(2L, 10L, 1L);

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(commentRepository.findAllByTaskIdOrderByTimestampAsc(10L, Pageable.unpaged())).thenReturn(List.of(first, second));
        when(commentMapper.toDto(first)).thenReturn(firstDto);
        when(commentMapper.toDto(second)).thenReturn(secondDto);

        List<CommentResponseDto> actual = commentService.getCommentsByTaskId(10L);

        assertThat(actual).containsExactly(firstDto, secondDto);
    }

    @Test
    void deleteComment_shouldAllowOwner() {
        User owner = TestDataFactory.standardUser(1L);
        Comment comment = TestDataFactory.comment(5L,
                TestDataFactory.task(10L, TestDataFactory.project(3L, owner)), owner);

        when(commentRepository.findById(5L)).thenReturn(Optional.of(comment));
        when(userService.getAuthenticatedUserOrThrow()).thenReturn(owner);

        commentService.deleteComment(5L);

        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_shouldAllowAdmin() {
        User owner = TestDataFactory.standardUser(1L);
        User admin = TestDataFactory.admin(2L);
        Comment comment = TestDataFactory.comment(5L,
                TestDataFactory.task(10L, TestDataFactory.project(3L, owner)), owner);

        when(commentRepository.findById(5L)).thenReturn(Optional.of(comment));
        when(userService.getAuthenticatedUserOrThrow()).thenReturn(admin);

        commentService.deleteComment(5L);

        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_shouldRejectNonOwnerNonAdmin() {
        User owner = TestDataFactory.standardUser(1L);
        User anotherUser = TestDataFactory.standardUser(2L);
        Comment comment = TestDataFactory.comment(5L,
                TestDataFactory.task(10L, TestDataFactory.project(3L, owner)), owner);

        when(commentRepository.findById(5L)).thenReturn(Optional.of(comment));
        when(userService.getAuthenticatedUserOrThrow()).thenReturn(anotherUser);

        assertThatThrownBy(() -> commentService.deleteComment(5L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("You are not allowed to delete this comment");

        verify(commentRepository, never()).delete(comment);
    }
}
