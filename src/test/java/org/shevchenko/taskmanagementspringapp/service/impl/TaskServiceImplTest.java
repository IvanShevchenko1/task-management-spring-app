package org.shevchenko.taskmanagementspringapp.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskUpdateRequestDto;
import org.shevchenko.taskmanagementspringapp.exception.EntityNotFoundException;
import org.shevchenko.taskmanagementspringapp.mapper.TaskMapper;
import org.shevchenko.taskmanagementspringapp.model.Project;
import org.shevchenko.taskmanagementspringapp.model.Task;
import org.shevchenko.taskmanagementspringapp.repository.ProjectRepository;
import org.shevchenko.taskmanagementspringapp.repository.TaskRepository;
import org.shevchenko.taskmanagementspringapp.util.TestDataFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void createTask_shouldAttachProjectAndReturnDto() {
        Long projectId = 10L;
        TaskCreateRequestDto requestDto = TestDataFactory.taskCreateRequest();
        Project project = TestDataFactory.project(projectId, TestDataFactory.standardUser(1L));
        Task mappedTask = new Task();
        Task savedTask = TestDataFactory.task(5L, project);
        TaskResponseDto responseDto = TestDataFactory.taskResponse(5L, projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskMapper.toModel(requestDto)).thenReturn(mappedTask);
        when(taskRepository.save(mappedTask)).thenReturn(savedTask);
        when(taskMapper.toDto(savedTask)).thenReturn(responseDto);

        TaskResponseDto actual = taskService.createTask(projectId, requestDto);

        assertThat(actual).isEqualTo(responseDto);
        assertThat(mappedTask.getProject()).isEqualTo(project);
        verify(taskRepository).save(mappedTask);
    }

    @Test
    void createTask_shouldThrowWhenProjectDoesNotExist() {
        Long projectId = 404L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.createTask(projectId, TestDataFactory.taskCreateRequest()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Can't find project by id: 404");

        verify(taskMapper, never()).toModel(any());
        verify(taskRepository, never()).save(any());
    }

    @Test
    void getAllTasksById_shouldMapEachTaskToDto() {
        Long projectId = 10L;
        Pageable pageable = PageRequest.of(0, 10);
        Project project = TestDataFactory.project(projectId, TestDataFactory.standardUser(1L));
        Task firstTask = TestDataFactory.task(1L, project);
        Task secondTask = TestDataFactory.task(2L, project);
        TaskResponseDto firstDto = TestDataFactory.taskResponse(1L, projectId);
        TaskResponseDto secondDto = TestDataFactory.taskResponse(2L, projectId);
        Page<Task> page = new PageImpl<>(List.of(firstTask, secondTask), pageable, 2);

        when(taskRepository.findAllByProjectId(projectId, pageable)).thenReturn(page);
        when(taskMapper.toDto(firstTask)).thenReturn(firstDto);
        when(taskMapper.toDto(secondTask)).thenReturn(secondDto);

        Page<TaskResponseDto> actual = taskService.getAllTasksById(projectId, pageable);

        assertThat(actual.getContent()).containsExactly(firstDto, secondDto);
    }

    @Test
    void getTaskById_shouldReturnMappedTask() {
        Task task = TestDataFactory.task(3L, TestDataFactory.project(7L, TestDataFactory.standardUser(1L)));
        TaskResponseDto responseDto = TestDataFactory.taskResponse(3L, 7L);

        when(taskRepository.findById(3L)).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(responseDto);

        TaskResponseDto actual = taskService.getTaskById(3L);

        assertThat(actual).isEqualTo(responseDto);
    }

    @Test
    void getTaskById_shouldThrowWhenTaskMissing() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Can't find task by id: 99");
    }

    @Test
    void updateTask_shouldApplyMapperAndSave() {
        TaskUpdateRequestDto requestDto = TestDataFactory.taskUpdateRequest();
        Project project = TestDataFactory.project(10L, TestDataFactory.standardUser(1L));
        Task task = TestDataFactory.task(3L, project);
        TaskResponseDto responseDto = TestDataFactory.taskResponse(3L, 10L);

        when(taskRepository.findById(3L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(responseDto);

        TaskResponseDto actual = taskService.updateTask(3L, requestDto);

        assertThat(actual).isEqualTo(responseDto);
        verify(taskMapper).updateEntity(requestDto, task);
        verify(taskRepository).save(task);
    }

    @Test
    void updateTask_shouldThrowWhenTaskMissing() {
        when(taskRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(77L, TestDataFactory.taskUpdateRequest()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Can't find task by id: 77");

        verify(taskMapper, never()).updateEntity(any(), any());
    }

    @Test
    void deleteTask_shouldDeleteById() {
        taskService.deleteTask(6L);

        verify(taskRepository).deleteById(6L);
    }
}
