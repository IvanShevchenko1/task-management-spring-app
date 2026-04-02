package org.shevchenko.taskmanagementspringapp.dto.task;

import java.time.LocalDate;
import org.shevchenko.taskmanagementspringapp.model.Task;

public record TaskResponseDto(
        Long id,
        String name,
        Task.Priority priority,
        Task.Status status,
        LocalDate dueDate,
        Long projectId
) {
}
