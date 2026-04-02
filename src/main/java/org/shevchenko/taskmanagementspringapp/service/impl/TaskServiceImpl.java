package org.shevchenko.taskmanagementspringapp.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskUpdateRequestDto;
import org.shevchenko.taskmanagementspringapp.mapper.TaskMapper;
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

    @Override
    public TaskResponseDto createTask(TaskCreateRequestDto requestDto) {
        return null;
    }

    @Override
    public List<TaskResponseDto> getAllTasks() {
        return List.of();
    }

    @Override
    public TaskResponseDto getTaskById(Long id) {
        return null;
    }

    @Override
    public TaskResponseDto updateTask(Long id, TaskUpdateRequestDto requestDto) {
        return null;
    }

    @Override
    public void deleteTask(Long id) {

    }
}
