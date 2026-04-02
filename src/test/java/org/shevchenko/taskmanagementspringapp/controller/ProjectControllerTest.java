package org.shevchenko.taskmanagementspringapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shevchenko.taskmanagementspringapp.dto.project.ProjectCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.project.ProjectResponseDto;
import org.shevchenko.taskmanagementspringapp.service.ProjectService;
import org.shevchenko.taskmanagementspringapp.support.TestDataFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {
    @Mock
    private ProjectService projectService;
    @InjectMocks
    private ProjectController projectController;

    @Test
    void createProject_shouldDelegateToService() {
        ProjectCreateRequestDto requestDto = TestDataFactory.projectCreateRequest();
        ProjectResponseDto responseDto = TestDataFactory.projectResponse(1L, 5L);
        when(projectService.createProject(requestDto)).thenReturn(responseDto);

        assertThat(projectController.createProject(requestDto)).isEqualTo(responseDto);
    }

    @Test
    void getAllProjectsForAuthenticatedUser_shouldDelegateToService() {
        PageRequest pageable = PageRequest.of(0, 5);
        Page<ProjectResponseDto> page = new PageImpl<>(java.util.List.of(TestDataFactory.projectResponse(1L, 5L)));
        when(projectService.getAllProjectsForAuthenticatedUser(pageable)).thenReturn(page);

        assertThat(projectController.getAllProjectsForAuthenticatedUser(pageable)).isEqualTo(page);
    }

    @Test
    void getProjectById_shouldDelegateToService() {
        ProjectResponseDto responseDto = TestDataFactory.projectResponse(4L, 5L);
        when(projectService.getProjectById(4L)).thenReturn(responseDto);

        assertThat(projectController.getProjectById(4L)).isEqualTo(responseDto);
    }

    @Test
    void updateProjectById_shouldDelegateToService() {
        ProjectCreateRequestDto requestDto = TestDataFactory.projectCreateRequest();
        ProjectResponseDto responseDto = TestDataFactory.projectResponse(4L, 5L);
        when(projectService.updateProjectById(4L, requestDto)).thenReturn(responseDto);

        assertThat(projectController.updateProjectById(4L, requestDto)).isEqualTo(responseDto);
    }

    @Test
    void deleteProjectById_shouldDelegateToService() {
        projectController.deleteProjectById(4L);
        verify(projectService).deleteProjectById(4L);
    }
}
