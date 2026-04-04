package org.shevchenko.taskmanagementspringapp.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.shevchenko.taskmanagementspringapp.config.SecurityConfig;
import org.shevchenko.taskmanagementspringapp.security.JwtAuthenticationFilter;
import org.shevchenko.taskmanagementspringapp.service.LabelService;
import org.shevchenko.taskmanagementspringapp.support.TestDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LabelController.class)
@Import(SecurityConfig.class)
class LabelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LabelService labelService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(authorities = "USER")
    void create_shouldReturnCreatedLabel() throws Exception {
        var requestDto = TestDataFactory.labelCreateRequest();
        var responseDto = TestDataFactory.labelResponse(1L);

        when(labelService.create(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/labels")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Backend"))
                .andExpect(jsonPath("$.color").value("#2563EB"));

        verify(labelService).create(requestDto);
    }

    @Test
    @WithMockUser(authorities = "USER")
    void getAll_shouldReturnLabels() throws Exception {
        var response = java.util.List.of(TestDataFactory.labelResponse(1L));
        when(labelService.getAll()).thenReturn(response);

        mockMvc.perform(get("/labels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Backend"));

        verify(labelService).getAll();
    }

    @Test
    @WithMockUser(authorities = "USER")
    void update_shouldReturnUpdatedLabel() throws Exception {
        var requestDto = TestDataFactory.labelCreateRequest();
        var responseDto = TestDataFactory.labelResponse(1L);

        when(labelService.update(1L, requestDto)).thenReturn(responseDto);

        mockMvc.perform(put("/labels/1")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Backend"))
                .andExpect(jsonPath("$.color").value("#2563EB"));

        verify(labelService).update(1L, requestDto);
    }

    @Test
    @WithMockUser(authorities = "USER")
    void delete_shouldReturnNoContent() throws Exception {
        doNothing().when(labelService).delete(1L);

        mockMvc.perform(delete("/labels/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(labelService).delete(1L);
    }
}
