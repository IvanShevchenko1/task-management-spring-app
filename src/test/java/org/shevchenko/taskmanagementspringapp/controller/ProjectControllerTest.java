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
import org.shevchenko.taskmanagementspringapp.service.ProjectService;
import org.shevchenko.taskmanagementspringapp.support.TestDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProjectController.class)
@Import(SecurityConfig.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(authorities = "USER")
    void createProject_shouldReturnCreatedProject() throws Exception {
        var requestDto = TestDataFactory.projectCreateRequest();
        var responseDto = TestDataFactory.projectResponse(1L, 5L);

        when(projectService.createProject(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/projects")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ownerId").value(5))
                .andExpect(jsonPath("$.name").value("Task Management API"));

        verify(projectService).createProject(requestDto);
    }

    @Test
    @WithMockUser(authorities = "USER")
    void getAllProjectsForAuthenticatedUser_shouldReturnPage() throws Exception {
        var pageable = PageRequest.of(0, 5);
        var page = new PageImpl<>(List.of(TestDataFactory.projectResponse(1L, 5L)), pageable, 1);

        when(projectService.getAllProjectsForAuthenticatedUser(org.mockito.ArgumentMatchers.any())).thenReturn(page);

        mockMvc.perform(get("/projects")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].ownerId").value(5))
                .andExpect(jsonPath("$.content[0].name").value("Task Management API"));
    }

    @Test
    @WithMockUser(authorities = "USER")
    void getProjectById_shouldReturnProject() throws Exception {
        var responseDto = TestDataFactory.projectResponse(4L, 5L);
        when(projectService.getProjectById(4L)).thenReturn(responseDto);

        mockMvc.perform(get("/projects/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.ownerId").value(5));

        verify(projectService).getProjectById(4L);
    }

    @Test
    @WithMockUser(authorities = "USER")
    void updateProjectById_shouldReturnUpdatedProject() throws Exception {
        var requestDto = TestDataFactory.projectCreateRequest();
        var responseDto = TestDataFactory.projectResponse(4L, 5L);

        when(projectService.updateProjectById(4L, requestDto)).thenReturn(responseDto);

        mockMvc.perform(put("/projects/4")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.ownerId").value(5));

        verify(projectService).updateProjectById(4L, requestDto);
    }

    @Test
    @WithMockUser(authorities = "USER")
    void deleteProjectById_shouldReturnNoContent() throws Exception {
        doNothing().when(projectService).deleteProjectById(4L);

        mockMvc.perform(delete("/projects/4").with(csrf()))
                .andExpect(status().isNoContent());

        verify(projectService).deleteProjectById(4L);
    }
}
