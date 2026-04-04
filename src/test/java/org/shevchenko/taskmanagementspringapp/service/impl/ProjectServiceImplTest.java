package org.shevchenko.taskmanagementspringapp.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shevchenko.taskmanagementspringapp.dto.project.ProjectCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.project.ProjectResponseDto;
import org.shevchenko.taskmanagementspringapp.exception.EntityNotFoundException;
import org.shevchenko.taskmanagementspringapp.mapper.ProjectMapper;
import org.shevchenko.taskmanagementspringapp.model.Project;
import org.shevchenko.taskmanagementspringapp.model.User;
import org.shevchenko.taskmanagementspringapp.repository.ProjectRepository;
import org.shevchenko.taskmanagementspringapp.service.UserService;
import org.shevchenko.taskmanagementspringapp.util.TestDataFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMapper projectMapper;
    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    void createProject_shouldAssignAuthenticatedOwner() {
        ProjectCreateRequestDto requestDto = TestDataFactory.projectCreateRequest();
        User user = TestDataFactory.standardUser(1L);
        Project mappedProject = new Project();
        Project savedProject = TestDataFactory.project(5L, user);
        ProjectResponseDto responseDto = TestDataFactory.projectResponse(5L, 1L);

        when(projectMapper.toModel(requestDto)).thenReturn(mappedProject);
        when(userService.getAuthenticatedUserOrThrow()).thenReturn(user);
        when(projectRepository.save(mappedProject)).thenReturn(savedProject);
        when(projectMapper.toDto(savedProject)).thenReturn(responseDto);

        ProjectResponseDto actual = projectService.createProject(requestDto);

        assertThat(actual).isEqualTo(responseDto);
        assertThat(mappedProject.getOwner()).isEqualTo(user);
    }

    @Test
    void getAllProjectsForAuthenticatedUser_shouldMapPage() {
        Pageable pageable = PageRequest.of(0, 10);
        User user = TestDataFactory.standardUser(7L);
        Project project = TestDataFactory.project(3L, user);
        ProjectResponseDto responseDto = TestDataFactory.projectResponse(3L, 7L);
        Page<Project> page = new PageImpl<>(java.util.List.of(project), pageable, 1);

        when(userService.getAuthenticatedUserOrThrow()).thenReturn(user);
        when(projectRepository.findAllByOwnerId(7L, pageable)).thenReturn(page);
        when(projectMapper.toDto(project)).thenReturn(responseDto);

        Page<ProjectResponseDto> actual = projectService.getAllProjectsForAuthenticatedUser(pageable);

        assertThat(actual.getContent()).containsExactly(responseDto);
    }

    @Test
    void getProjectById_shouldReturnMappedDto() {
        Project project = TestDataFactory.project(11L, TestDataFactory.standardUser(1L));
        ProjectResponseDto responseDto = TestDataFactory.projectResponse(11L, 1L);

        when(projectRepository.findById(11L)).thenReturn(Optional.of(project));
        when(projectMapper.toDto(project)).thenReturn(responseDto);

        assertThat(projectService.getProjectById(11L)).isEqualTo(responseDto);
    }

    @Test
    void getProjectById_shouldReturnNullWhenMissingBecauseCurrentImplementationMapsNull() {
        when(projectRepository.findById(11L)).thenReturn(Optional.empty());
        when(projectMapper.toDto(null)).thenReturn(null);

        assertThat(projectService.getProjectById(11L)).isNull();
        verify(projectMapper).toDto(null);
    }

    @Test
    void updateProjectById_shouldApplyMapperAndSave() {
        ProjectCreateRequestDto requestDto = TestDataFactory.projectCreateRequest();
        Project project = TestDataFactory.project(9L, TestDataFactory.standardUser(1L));
        ProjectResponseDto responseDto = TestDataFactory.projectResponse(9L, 1L);

        when(projectRepository.findById(9L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(responseDto);

        ProjectResponseDto actual = projectService.updateProjectById(9L, requestDto);

        assertThat(actual).isEqualTo(responseDto);
        verify(projectMapper).updateEntity(requestDto, project);
    }

    @Test
    void updateProjectById_shouldThrowWhenProjectMissing() {
        when(projectRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.updateProjectById(100L, TestDataFactory.projectCreateRequest()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Project not found by id 100");

        verify(projectMapper, never()).updateEntity(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deleteProjectById_shouldDelegateToRepository() {
        projectService.deleteProjectById(55L);

        verify(projectRepository).deleteById(55L);
    }
}
