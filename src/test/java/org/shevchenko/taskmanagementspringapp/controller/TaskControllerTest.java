package org.shevchenko.taskmanagementspringapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shevchenko.taskmanagementspringapp.dto.label.AssignLabelsToTaskRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.label.LabelResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskUpdateRequestDto;
import org.shevchenko.taskmanagementspringapp.service.LabelService;
import org.shevchenko.taskmanagementspringapp.service.TaskService;
import org.shevchenko.taskmanagementspringapp.support.TestDataFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {
    @Mock
    private TaskService taskService;
    @Mock
    private LabelService labelService;
    @InjectMocks
    private TaskController taskController;

    @Test
    void createTask_shouldDelegateToService() {
        TaskCreateRequestDto requestDto = TestDataFactory.taskCreateRequest();
        TaskResponseDto responseDto = TestDataFactory.taskResponse(1L, 9L);
        when(taskService.createTask(9L, requestDto)).thenReturn(responseDto);

        assertThat(taskController.createTask(9L, requestDto)).isEqualTo(responseDto);
        verify(taskService).createTask(9L, requestDto);
    }

    @Test
    void getAllTasks_shouldDelegateToService() {
        List<TaskResponseDto> responses = List.of(TestDataFactory.taskResponse(1L, 9L));
        when(taskService.getAllTasksById(9L)).thenReturn(responses);

        assertThat(taskController.getAllTasks(9L)).isEqualTo(responses);
    }

    @Test
    void getTaskById_shouldDelegateToService() {
        TaskResponseDto responseDto = TestDataFactory.taskResponse(1L, 9L);
        when(taskService.getTaskById(1L)).thenReturn(responseDto);

        assertThat(taskController.getTaskById(1L)).isEqualTo(responseDto);
    }

    @Test
    void updateTask_shouldDelegateToService() {
        TaskUpdateRequestDto requestDto = TestDataFactory.taskUpdateRequest();
        TaskResponseDto responseDto = TestDataFactory.taskResponse(1L, 9L);
        when(taskService.updateTask(1L, requestDto)).thenReturn(responseDto);

        assertThat(taskController.updateTask(1L, requestDto)).isEqualTo(responseDto);
    }

    @Test
    void deleteTask_shouldDelegateToService() {
        taskController.deleteTask(1L);
        verify(taskService).deleteTask(1L);
    }

    @Test
    void assignLabels_shouldDelegateToLabelService() {
        AssignLabelsToTaskRequestDto requestDto = new AssignLabelsToTaskRequestDto(Set.of(1L, 2L));
        List<LabelResponseDto> response = List.of(TestDataFactory.labelResponse(1L));
        when(labelService.assignToTask(11L, Set.of(1L, 2L))).thenReturn(response);

        assertThat(taskController.assignLabels(11L, requestDto)).isEqualTo(response);
    }

    @Test
    void createTask_shouldHaveCreatedStatusAnnotation() throws NoSuchMethodException {
        ResponseStatus responseStatus = TaskController.class
                .getMethod("createTask", Long.class, TaskCreateRequestDto.class)
                .getAnnotation(ResponseStatus.class);

        assertThat(responseStatus.value()).isEqualTo(HttpStatus.CREATED);
    }
}
