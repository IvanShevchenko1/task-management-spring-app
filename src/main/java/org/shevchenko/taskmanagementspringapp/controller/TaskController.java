package org.shevchenko.taskmanagementspringapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.shevchenko.taskmanagementspringapp.dto.label.AssignLabelsToTaskRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.label.LabelResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskUpdateRequestDto;
import org.shevchenko.taskmanagementspringapp.service.LabelService;
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
@Tag(name = "Tasks", description = "Endpoints for managing project tasks")
public class TaskController {
    private final TaskService taskService;
    private final LabelService labelService;

    @Tag(name = "Tasks", description = "Endpoints for managing project tasks")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDto createTask(
            @PathVariable Long projectId,
            @RequestBody @Valid TaskCreateRequestDto requestDto) {
        return taskService.createTask(projectId, requestDto);
    }

    @Operation(summary = "Get all tasks for project")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TaskResponseDto> getAllTasks(@PathVariable Long projectId) {
        return taskService.getAllTasksById(projectId);
    }

    @Operation(summary = "Get task by id")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponseDto getTaskById(@PathVariable Long taskId) {
        return taskService.getTaskById(taskId);
    }

    @Operation(summary = "Update task by id")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponseDto updateTask(
            @PathVariable Long id,
            @RequestBody @Valid TaskUpdateRequestDto requestDto
    ) {
        return taskService.updateTask(id, requestDto);
    }

    @Operation(summary = "Delete task by id")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PutMapping("/{taskId}/labels")
    public List<LabelResponseDto> assignLabels(
            @PathVariable Long taskId,
            @RequestBody @Valid AssignLabelsToTaskRequestDto requestDto
    ) {
        return labelService.assignToTask(taskId, requestDto.labelIds());
    }
}
