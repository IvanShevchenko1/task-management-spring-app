package org.shevchenko.taskmanagementspringapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.shevchenko.taskmanagementspringapp.dto.project.ProjectCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.project.ProjectResponseDto;
import org.shevchenko.taskmanagementspringapp.exception.EntityNotFoundException;
import org.shevchenko.taskmanagementspringapp.mapper.ProjectMapper;
import org.shevchenko.taskmanagementspringapp.model.Project;
import org.shevchenko.taskmanagementspringapp.model.User;
import org.shevchenko.taskmanagementspringapp.repository.ProjectRepository;
import org.shevchenko.taskmanagementspringapp.service.ProjectService;
import org.shevchenko.taskmanagementspringapp.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final UserService userService;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectResponseDto createProject(ProjectCreateRequestDto request) {
        Project newProject = projectMapper.toModel(request);
        User user = userService.getAuthenticatedUserOrThrow();
        newProject.setOwner(user);
        return projectMapper.toDto(projectRepository.save(newProject));
    }

    @Override
    public Page<ProjectResponseDto> getAllProjectsForAuthenticatedUser(Pageable pageable) {
        User user = userService.getAuthenticatedUserOrThrow();
        return projectRepository.findAllByOwnerId(user.getId(), pageable)
                .map(projectMapper::toDto);
    }

    @Override
    public ProjectResponseDto getProjectById(Long id) {
        return projectMapper.toDto(projectRepository.findById(id).orElse(null));
    }

    @Override
    public ProjectResponseDto updateProjectById(Long id, ProjectCreateRequestDto request) {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Project not found by id " + id));
        projectMapper.updateEntity(request, project);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    public void deleteProjectById(Long id) {
        projectRepository.deleteById(id);
    }
}
