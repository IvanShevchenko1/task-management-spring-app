package org.shevchenko.taskmanagementspringapp.dto.label;

import jakarta.validation.constraints.NotBlank;

public record LabelCreateRequestDto(
        @NotBlank
        String name,
        @NotBlank
        String color
) {
}
