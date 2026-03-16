package org.shevchenko.taskmanagementspringapp.service;

import org.shevchenko.taskmanagementspringapp.dto.project.ProjectCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.project.ProjectResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    ProjectResponseDto createProject(ProjectCreateRequestDto request);

    Page<ProjectResponseDto> getAllProjectsForAuthenticatedUser(Pageable pageable);

    ProjectResponseDto getProjectById(Long id);

    ProjectResponseDto updateProjectById(Long id, ProjectCreateRequestDto request);

    void deleteProjectById(Long id);
}
