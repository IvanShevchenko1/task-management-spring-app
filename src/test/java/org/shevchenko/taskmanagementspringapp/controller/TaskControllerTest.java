package org.shevchenko.taskmanagementspringapp.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.shevchenko.taskmanagementspringapp.config.SecurityConfig;
import org.shevchenko.taskmanagementspringapp.security.JwtAuthenticationFilter;
import org.shevchenko.taskmanagementspringapp.service.TaskService;
import org.shevchenko.taskmanagementspringapp.support.TestDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(authorities = "USER")
    void createTask_shouldReturnCreatedTask() throws Exception {
        var requestDto = TestDataFactory.taskCreateRequest();
        var responseDto = TestDataFactory.taskResponse(1L, 9L);

        when(taskService.createTask(9L, requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/projects/9/tasks")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.projectId").value(9))
                .andExpect(jsonPath("$.name").value("Implement tests"));

        verify(taskService).createTask(9L, requestDto);
    }

    @Test
    @WithMockUser(authorities = "USER")
    void getAllTasks_shouldReturnTasks() throws Exception {
        var response = List.of(TestDataFactory.taskResponse(1L, 9L));
        when(taskService.getAllTasksById(9L)).thenReturn(response);

        mockMvc.perform(get("/projects/9/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].projectId").value(9))
                .andExpect(jsonPath("$[0].name").value("Implement tests"));

        verify(taskService).getAllTasksById(9L);
    }

    @Test
    @WithMockUser(authorities = "USER")
    void getTaskById_shouldReturnTask() throws Exception {
        var responseDto = TestDataFactory.taskResponse(1L, 9L);
        when(taskService.getTaskById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/projects/9/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.projectId").value(9));

        verify(taskService).getTaskById(1L);
    }

    @Test
    @WithMockUser(authorities = "USER")
    void updateTask_shouldReturnUpdatedTask() throws Exception {
        var requestDto = TestDataFactory.taskUpdateRequest();
        var responseDto = TestDataFactory.taskResponse(1L, 9L);

        when(taskService.updateTask(1L, requestDto)).thenReturn(responseDto);

        mockMvc.perform(put("/projects/9/tasks/1")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.projectId").value(9));

        verify(taskService).updateTask(1L, requestDto);
    }

    @Test
    @WithMockUser(authorities = "USER")
    void deleteTask_shouldReturnNoContent() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/projects/9/tasks/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(1L);
    }
}
