package org.shevchenko.taskmanagementspringapp.service;

import org.shevchenko.taskmanagementspringapp.dto.task.TaskCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskUpdateRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskResponseDto createTask(Long projectId, TaskCreateRequestDto requestDto);

    Page<TaskResponseDto> getAllTasksById(Long projectId, Pageable pageable);

    TaskResponseDto getTaskById(Long id);

    TaskResponseDto updateTask(Long id, TaskUpdateRequestDto requestDto);

    void deleteTask(Long id);
}
