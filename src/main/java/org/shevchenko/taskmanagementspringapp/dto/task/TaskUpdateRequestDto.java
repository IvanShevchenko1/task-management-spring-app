package org.shevchenko.taskmanagementspringapp.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.shevchenko.taskmanagementspringapp.model.Task;

public record TaskUpdateRequestDto(
        @NotBlank
        String name,
        @NotNull
        Task.Priority priority,
        @NotNull
        Task.Status status,
        @NotNull
        LocalDate dueDate
) {
}
