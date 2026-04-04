package org.shevchenko.taskmanagementspringapp.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.shevchenko.taskmanagementspringapp.config.SecurityConfig;
import org.shevchenko.taskmanagementspringapp.security.JwtAuthenticationFilter;
import org.shevchenko.taskmanagementspringapp.service.UserService;
import org.shevchenko.taskmanagementspringapp.support.TestDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(authorities = "USER")
    void getAuthenticatedUser_shouldReturnCurrentUser() throws Exception {
        var responseDto = TestDataFactory.userResponse(7L);
        when(userService.getAuthenticatedUser()).thenReturn(responseDto);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.roles[0]").value("USER"));

        verify(userService).getAuthenticatedUser();
    }

    @Test
    @WithMockUser(authorities = "USER")
    void updateAuthenticatedUser_shouldReturnUpdatedUser() throws Exception {
        var requestDto = TestDataFactory.userUpdateRequest();
        var responseDto = TestDataFactory.userResponse(7L);

        when(userService.updateAuthenticatedUser(requestDto)).thenReturn(responseDto);

        mockMvc.perform(put("/users/me")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.username").value("johnny"));

        verify(userService).updateAuthenticatedUser(requestDto);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void updateRole_shouldReturnUpdatedUserRole() throws Exception {
        var requestDto = TestDataFactory.userUpdateRoleRequest("ADMIN");
        var responseDto = TestDataFactory.userResponse(7L);

        when(userService.updateRole(7L, requestDto)).thenReturn(responseDto);

        mockMvc.perform(put("/users/7/role")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService).updateRole(7L, requestDto);
    }

    @Test
    @WithMockUser(authorities = "USER")
    void updateRole_shouldReturnForbiddenForNonAdmin() throws Exception {
        var requestDto = TestDataFactory.userUpdateRoleRequest("ADMIN");

        mockMvc.perform(put("/users/7/role")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }
}
