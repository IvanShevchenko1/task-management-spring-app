package org.shevchenko.taskmanagementspringapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shevchenko.taskmanagementspringapp.dto.comment.CommentCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.comment.CommentResponseDto;
import org.shevchenko.taskmanagementspringapp.service.CommentService;
import org.shevchenko.taskmanagementspringapp.support.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {
    @Mock
    private CommentService commentService;
    @InjectMocks
    private CommentController commentController;

    @Test
    void addComment_shouldDelegateToService() {
        CommentCreateRequestDto requestDto = TestDataFactory.commentCreateRequest(10L);
        CommentResponseDto responseDto = TestDataFactory.commentResponse(1L, 10L, 7L);
        when(commentService.addComment(requestDto)).thenReturn(responseDto);

        assertThat(commentController.addComment(requestDto)).isEqualTo(responseDto);
    }

    @Test
    void getCommentsByTaskId_shouldDelegateToService() {
        List<CommentResponseDto> responses = List.of(TestDataFactory.commentResponse(1L, 10L, 7L));
        when(commentService.getCommentsByTaskId(10L)).thenReturn(responses);

        assertThat(commentController.getCommentsByTaskId(10L)).isEqualTo(responses);
    }

    @Test
    void deleteComment_shouldDelegateToService() {
        commentController.deleteComment(9L);
        verify(commentService).deleteComment(9L);
    }
}
