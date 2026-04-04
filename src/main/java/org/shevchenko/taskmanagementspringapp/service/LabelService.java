package org.shevchenko.taskmanagementspringapp.service;

import java.util.Set;
import org.shevchenko.taskmanagementspringapp.dto.label.LabelCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.label.LabelResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LabelService {
    LabelResponseDto create(LabelCreateRequestDto requestDto);

    Page<LabelResponseDto> getAll(Pageable pageable);

    LabelResponseDto update(Long id, LabelCreateRequestDto requestDto);

    void delete(Long id);

    void assignToTask(Long taskId, Set<Long> labelIds);

    Page<LabelResponseDto> getLabelsByTaskId(Long taskId, Pageable pageable);
}
