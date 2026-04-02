package org.shevchenko.taskmanagementspringapp.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskUpdateRequestDto;
import org.shevchenko.taskmanagementspringapp.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {
    private final TaskService taskService;

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDto createTask(
            @PathVariable Long projectId,
            @RequestBody @Valid TaskCreateRequestDto requestDto) {
        return taskService.createTask(projectId, requestDto);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TaskResponseDto> getAllTasks(@PathVariable Long projectId) {
        return taskService.getAllTasksById(projectId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponseDto getTaskById(@PathVariable Long taskId) {
        return taskService.getTaskById(taskId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponseDto updateTask(
            @PathVariable Long id,
            @RequestBody @Valid TaskUpdateRequestDto requestDto
    ) {
        return taskService.updateTask(id, requestDto);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
