package org.shevchenko.taskmanagementspringapp.dto.label;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public record AssignLabelsToTaskRequestDto(
        @NotEmpty Set<Long> labelIds
) {
}
