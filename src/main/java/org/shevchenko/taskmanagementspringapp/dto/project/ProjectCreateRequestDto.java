package org.shevchenko.taskmanagementspringapp.dto.project;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectCreateRequestDto {
    @NotBlank
    private String name;
    private String description;
}
