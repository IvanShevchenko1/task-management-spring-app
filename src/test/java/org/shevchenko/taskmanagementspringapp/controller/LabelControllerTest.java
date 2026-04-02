package org.shevchenko.taskmanagementspringapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shevchenko.taskmanagementspringapp.dto.label.LabelCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.label.LabelResponseDto;
import org.shevchenko.taskmanagementspringapp.service.LabelService;
import org.shevchenko.taskmanagementspringapp.support.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class LabelControllerTest {
    @Mock
    private LabelService labelService;
    @InjectMocks
    private LabelController labelController;

    @Test
    void create_shouldDelegateToService() {
        LabelCreateRequestDto requestDto = TestDataFactory.labelCreateRequest();
        LabelResponseDto responseDto = TestDataFactory.labelResponse(1L);
        when(labelService.create(requestDto)).thenReturn(responseDto);

        assertThat(labelController.create(requestDto)).isEqualTo(responseDto);
    }

    @Test
    void getAll_shouldDelegateToService() {
        List<LabelResponseDto> responses = List.of(TestDataFactory.labelResponse(1L));
        when(labelService.getAll()).thenReturn(responses);

        assertThat(labelController.getAll()).isEqualTo(responses);
    }

    @Test
    void update_shouldDelegateToService() {
        LabelCreateRequestDto requestDto = TestDataFactory.labelCreateRequest();
        LabelResponseDto responseDto = TestDataFactory.labelResponse(1L);
        when(labelService.update(1L, requestDto)).thenReturn(responseDto);

        assertThat(labelController.update(1L, requestDto)).isEqualTo(responseDto);
    }

    @Test
    void delete_shouldDelegateToService() {
        labelController.delete(1L);
        verify(labelService).delete(1L);
    }
}
