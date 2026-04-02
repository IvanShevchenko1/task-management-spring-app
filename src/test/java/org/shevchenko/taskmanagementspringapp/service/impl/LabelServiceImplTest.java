package org.shevchenko.taskmanagementspringapp.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shevchenko.taskmanagementspringapp.dto.label.LabelCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.label.LabelResponseDto;
import org.shevchenko.taskmanagementspringapp.exception.EntityNotFoundException;
import org.shevchenko.taskmanagementspringapp.mapper.LabelMapper;
import org.shevchenko.taskmanagementspringapp.model.Label;
import org.shevchenko.taskmanagementspringapp.model.Project;
import org.shevchenko.taskmanagementspringapp.model.Task;
import org.shevchenko.taskmanagementspringapp.model.User;
import org.shevchenko.taskmanagementspringapp.repository.LabelRepository;
import org.shevchenko.taskmanagementspringapp.repository.TaskRepository;
import org.shevchenko.taskmanagementspringapp.service.UserService;
import org.shevchenko.taskmanagementspringapp.support.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class LabelServiceImplTest {
    @Mock
    private LabelRepository labelRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private LabelMapper labelMapper;
    @Mock
    private UserService userService;
    @InjectMocks
    private LabelServiceImpl labelService;

    @Test
    void create_shouldAssignAuthenticatedUser() {
        User user = TestDataFactory.standardUser(1L);
        LabelCreateRequestDto requestDto = TestDataFactory.labelCreateRequest();
        Label mappedLabel = new Label();
        Label savedLabel = TestDataFactory.label(5L, user);
        LabelResponseDto responseDto = TestDataFactory.labelResponse(5L);

        when(userService.getAuthenticatedUserOrThrow()).thenReturn(user);
        when(labelMapper.toModel(requestDto)).thenReturn(mappedLabel);
        when(labelRepository.save(mappedLabel)).thenReturn(savedLabel);
        when(labelMapper.toDto(savedLabel)).thenReturn(responseDto);

        LabelResponseDto actual = labelService.create(requestDto);

        assertThat(actual).isEqualTo(responseDto);
        assertThat(mappedLabel.getUser()).isEqualTo(user);
    }

    @Test
    void getAll_shouldReturnOnlyAuthenticatedUsersLabels() {
        User user = TestDataFactory.standardUser(1L);
        Label first = TestDataFactory.label(1L, user);
        Label second = TestDataFactory.label(2L, user);
        LabelResponseDto firstDto = TestDataFactory.labelResponse(1L);
        LabelResponseDto secondDto = TestDataFactory.labelResponse(2L);

        when(userService.getAuthenticatedUserOrThrow()).thenReturn(user);
        when(labelRepository.findAllByUserId(1L)).thenReturn(List.of(first, second));
        when(labelMapper.toDto(first)).thenReturn(firstDto);
        when(labelMapper.toDto(second)).thenReturn(secondDto);

        List<LabelResponseDto> actual = labelService.getAll();

        assertThat(actual).containsExactly(firstDto, secondDto);
    }

    @Test
    void update_shouldModifyOwnedLabel() {
        User user = TestDataFactory.standardUser(1L);
        LabelCreateRequestDto requestDto = TestDataFactory.labelCreateRequest();
        Label label = TestDataFactory.label(3L, user);
        LabelResponseDto responseDto = TestDataFactory.labelResponse(3L);

        when(userService.getAuthenticatedUserOrThrow()).thenReturn(user);
        when(labelRepository.findByIdAndUserId(3L, 1L)).thenReturn(Optional.of(label));
        when(labelRepository.save(label)).thenReturn(label);
        when(labelMapper.toDto(label)).thenReturn(responseDto);

        LabelResponseDto actual = labelService.update(3L, requestDto);

        assertThat(actual).isEqualTo(responseDto);
        verify(labelMapper).updateEntity(requestDto, label);
    }

    @Test
    void delete_shouldRemoveOwnedLabel() {
        User user = TestDataFactory.standardUser(1L);
        Label label = TestDataFactory.label(3L, user);

        when(userService.getAuthenticatedUserOrThrow()).thenReturn(user);
        when(labelRepository.findByIdAndUserId(3L, 1L)).thenReturn(Optional.of(label));

        labelService.delete(3L);

        verify(labelRepository).delete(label);
    }

    @Test
    void assignToTask_shouldReplaceTaskLabelsForOwner() {
        User owner = TestDataFactory.standardUser(1L);
        Project project = TestDataFactory.project(2L, owner);
        Task task = TestDataFactory.task(10L, project);
        Label first = TestDataFactory.label(1L, owner);
        Label second = TestDataFactory.label(2L, owner);
        LabelResponseDto firstDto = TestDataFactory.labelResponse(1L);
        LabelResponseDto secondDto = TestDataFactory.labelResponse(2L);

        when(userService.getAuthenticatedUserOrThrow()).thenReturn(owner);
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(labelRepository.findAllByIdInAndUserId(Set.of(1L, 2L), 1L)).thenReturn(List.of(first, second));
        when(taskRepository.save(task)).thenReturn(task);
        when(labelMapper.toDto(first)).thenReturn(firstDto);
        when(labelMapper.toDto(second)).thenReturn(secondDto);

        List<LabelResponseDto> actual = labelService.assignToTask(10L, Set.of(1L, 2L));

        assertThat(actual).containsExactlyInAnyOrder(firstDto, secondDto);
        verify(taskRepository).save(task);
    }

    @Test
    void assignToTask_shouldRejectUnauthorizedUser() {
        User owner = TestDataFactory.standardUser(1L);
        User anotherUser = TestDataFactory.standardUser(2L);
        Task task = TestDataFactory.task(10L, TestDataFactory.project(2L, owner));

        when(userService.getAuthenticatedUserOrThrow()).thenReturn(anotherUser);
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> labelService.assignToTask(10L, Set.of(1L)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("You are not allowed to modify labels for this task");

        verify(labelRepository, never()).findAllByIdInAndUserId(any(), any());
    }

    @Test
    void assignToTask_shouldThrowWhenAnyLabelMissing() {
        User owner = TestDataFactory.standardUser(1L);
        Task task = TestDataFactory.task(10L, TestDataFactory.project(2L, owner));
        Label first = TestDataFactory.label(1L, owner);

        when(userService.getAuthenticatedUserOrThrow()).thenReturn(owner);
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(labelRepository.findAllByIdInAndUserId(Set.of(1L, 2L), 1L)).thenReturn(List.of(first));

        assertThatThrownBy(() -> labelService.assignToTask(10L, Set.of(1L, 2L)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("One or more labels were not found");
    }
}
