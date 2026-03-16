package org.shevchenko.taskmanagementspringapp.dto.project;

import java.time.LocalDateTime;
import org.shevchenko.taskmanagementspringapp.model.Project;

public record ProjectResponseDto(
        Long id,
        String name,
        String description,
        Project.Status status,
        LocalDateTime startDate,
        Long owner_id
) {
}
