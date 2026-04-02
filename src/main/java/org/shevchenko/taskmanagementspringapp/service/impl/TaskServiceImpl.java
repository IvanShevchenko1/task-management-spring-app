package org.shevchenko.taskmanagementspringapp.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskUpdateRequestDto;
import org.shevchenko.taskmanagementspringapp.exception.EntityNotFoundException;
import org.shevchenko.taskmanagementspringapp.mapper.TaskMapper;
import org.shevchenko.taskmanagementspringapp.model.Project;
import org.shevchenko.taskmanagementspringapp.model.Task;
import org.shevchenko.taskmanagementspringapp.repository.ProjectRepository;
import org.shevchenko.taskmanagementspringapp.repository.TaskRepository;
import org.shevchenko.taskmanagementspringapp.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public TaskResponseDto createTask(Long projectId, TaskCreateRequestDto requestDto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find project by id: " + projectId
                ));

        Task task = taskMapper.toModel(requestDto);
        task.setProject(project);

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public List<TaskResponseDto> getAllTasksById(Long projectId) {
        return taskRepository.findAllByProjectId(projectId).stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Override
    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find task by id: " + id
                ));
        return taskMapper.toDto(task);
    }

    @Override
    public TaskResponseDto updateTask(Long id, TaskUpdateRequestDto requestDto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find task by id: " + id
                ));

        taskMapper.updateEntity(requestDto, task);

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
