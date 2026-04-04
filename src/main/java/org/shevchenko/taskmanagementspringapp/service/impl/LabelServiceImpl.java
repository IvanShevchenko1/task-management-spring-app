package org.shevchenko.taskmanagementspringapp.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.shevchenko.taskmanagementspringapp.dto.label.LabelCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.label.LabelResponseDto;
import org.shevchenko.taskmanagementspringapp.exception.EntityNotFoundException;
import org.shevchenko.taskmanagementspringapp.exception.ForbiddenOperationException;
import org.shevchenko.taskmanagementspringapp.mapper.LabelMapper;
import org.shevchenko.taskmanagementspringapp.model.Label;
import org.shevchenko.taskmanagementspringapp.model.Task;
import org.shevchenko.taskmanagementspringapp.model.User;
import org.shevchenko.taskmanagementspringapp.repository.LabelRepository;
import org.shevchenko.taskmanagementspringapp.repository.TaskRepository;
import org.shevchenko.taskmanagementspringapp.service.LabelService;
import org.shevchenko.taskmanagementspringapp.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {
    private final LabelRepository labelRepository;
    private final TaskRepository taskRepository;
    private final LabelMapper labelMapper;
    private final UserService userService;

    @Override
    public LabelResponseDto create(LabelCreateRequestDto requestDto) {
        User user = userService.getAuthenticatedUserOrThrow();

        Label label = labelMapper.toModel(requestDto);
        label.setUser(user);

        return labelMapper.toDto(labelRepository.save(label));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LabelResponseDto> getAll(Pageable pageable) {
        User user = userService.getAuthenticatedUserOrThrow();

        return labelRepository.findAllByUserId(user.getId(), pageable)
                .map(labelMapper::toDto);
    }

    @Override
    public LabelResponseDto update(Long id, LabelCreateRequestDto requestDto) {
        User user = userService.getAuthenticatedUserOrThrow();

        Label label = labelRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find label by id: " + id));

        labelMapper.updateEntity(requestDto, label);

        return labelMapper.toDto(labelRepository.save(label));
    }

    @Override
    public void delete(Long id) {
        User user = userService.getAuthenticatedUserOrThrow();

        Label label = labelRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find label by id: " + id));

        labelRepository.delete(label);
    }

    @Override
    public void assignToTask(Long taskId, Set<Long> labelIds) {
        User user = userService.getAuthenticatedUserOrThrow();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find task by id: " + taskId));

        boolean isOwner = task.getProject().getOwner().getId().equals(user.getId());
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new ForbiddenOperationException("You are not allowed to modify labels for this task");
        }

        List<Label> labels = labelRepository.findAllByIdInAndUserId(labelIds, user.getId());

        if (labels.size() != labelIds.size()) {
            throw new EntityNotFoundException("One or more labels were not found");
        }

        task.setLabels(new HashSet<>(labels));
        taskRepository.save(task);
    }

    @Override
    public Page<LabelResponseDto> getLabelsByTaskId(Long taskId, Pageable pageable) {
        return labelRepository.findAllByTasksId(taskId, pageable)
                .map(labelMapper::toDto);
    }
}
