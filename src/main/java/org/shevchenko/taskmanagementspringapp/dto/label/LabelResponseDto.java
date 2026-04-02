package org.shevchenko.taskmanagementspringapp.dto.label;

public record LabelResponseDto(
        Long id,
        String name,
        String color
) {
}
