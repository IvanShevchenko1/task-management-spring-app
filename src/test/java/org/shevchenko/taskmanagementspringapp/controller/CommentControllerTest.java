package org.shevchenko.taskmanagementspringapp.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.shevchenko.taskmanagementspringapp.config.SecurityConfig;
import org.shevchenko.taskmanagementspringapp.security.JwtAuthenticationFilter;
import org.shevchenko.taskmanagementspringapp.service.CommentService;
import org.shevchenko.taskmanagementspringapp.support.TestDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CommentController.class)
@Import(SecurityConfig.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void addComment_shouldReturnUnauthorizedWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/comments")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                TestDataFactory.commentCreateRequest(10L))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "USER")
    void addComment_shouldReturnCreatedComment() throws Exception {
        var requestDto = TestDataFactory.commentCreateRequest(10L);
        var responseDto = TestDataFactory.commentResponse(1L, 10L, 7L);

        when(commentService.addComment(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/comments")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.taskId").value(10))
                .andExpect(jsonPath("$.userId").value(7))
                .andExpect(jsonPath("$.text").value("Looks good"));

        verify(commentService).addComment(requestDto);
    }

    @Test
    @WithMockUser(authorities = "USER")
    void getCommentsByTaskId_shouldReturnComments() throws Exception {
        var response = java.util.List.of(TestDataFactory.commentResponse(1L, 10L, 7L));
        when(commentService.getCommentsByTaskId(10L)).thenReturn(response);

        mockMvc.perform(get("/comments").param("taskId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].taskId").value(10))
                .andExpect(jsonPath("$[0].userId").value(7));

        verify(commentService).getCommentsByTaskId(10L);
    }

    @Test
    @WithMockUser(authorities = "USER")
    void deleteComment_shouldReturnNoContent() throws Exception {
        doNothing().when(commentService).deleteComment(9L);

        mockMvc.perform(delete("/comments/9").with(csrf()))
                .andExpect(status().isNoContent());

        verify(commentService).deleteComment(9L);
    }
}
