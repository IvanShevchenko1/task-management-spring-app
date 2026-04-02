package org.shevchenko.taskmanagementspringapp.service;

import java.util.List;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskUpdateRequestDto;

public interface TaskService {
    TaskResponseDto createTask(TaskCreateRequestDto requestDto);

    List<TaskResponseDto> getAllTasks();

    TaskResponseDto getTaskById(Long id);

    TaskResponseDto updateTask(Long id, TaskUpdateRequestDto requestDto);

    void deleteTask(Long id);
}
