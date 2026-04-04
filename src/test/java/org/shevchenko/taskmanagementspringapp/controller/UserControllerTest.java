package org.shevchenko.taskmanagementspringapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.shevchenko.taskmanagementspringapp.dto.user.UserResponseDto;
import org.shevchenko.taskmanagementspringapp.util.TestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = true)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataSource dataSource;

    @AfterEach
    void cleanup() {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection con = dataSource.getConnection()) {
            con.setAutoCommit(true);
            ScriptUtils.executeSqlScript(con,
                    new ClassPathResource("database/common/delete_test_data.sql"));
        }
    }

    @Test
    @DisplayName("GET /users/me without authentication -> 401 Unauthorized")
    void getAuthenticatedUser_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /users/me with USER authority -> 200 OK")
    @WithUserDetails(TestUtil.USER_EMAIL)
    @Sql(scripts = "classpath:database/common/insert_test_users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAuthenticatedUser_WithUser_ReturnsOk() throws Exception {
        MvcResult result = mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andReturn();

        UserResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class);

        assertEquals(2L, response.id());
        assertEquals(TestUtil.USER_EMAIL, response.email());
        assertTrue(response.roles().contains("USER"));
    }

    @Test
    @DisplayName("PUT /users/me with USER authority -> 200 OK")
    @WithUserDetails(TestUtil.USER_EMAIL)
    @Sql(scripts = "classpath:database/common/insert_test_users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateAuthenticatedUser_WithUser_ReturnsOk() throws Exception {
        MvcResult result = mockMvc.perform(put("/users/me")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(TestUtil.validUserUpdateRequest())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andReturn();

        UserResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class);

        assertEquals(2L, response.id());
        assertEquals("updated.user@example.com", response.email());
        assertEquals("updated.user@example.com", response.username());
        assertEquals("Updated", response.firstName());
        assertEquals("Name", response.lastName());
        assertTrue(response.roles().contains("USER"));
    }

    @Test
    @DisplayName("PUT /users/me with invalid body -> 400 Bad Request")
    @WithUserDetails(TestUtil.USER_EMAIL)
    @Sql(scripts = "classpath:database/common/insert_test_users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateAuthenticatedUser_WithInvalidBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/users/me")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(TestUtil.validUserUpdateRequest())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /users/{id}/role with ADMIN authority -> 200 OK")
    @WithUserDetails(TestUtil.ADMIN_EMAIL)
    @Sql(scripts = "classpath:database/common/insert_test_users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateRole_WithAdmin_ReturnsOk() throws Exception {
        MvcResult result = mockMvc.perform(put("/users/2/role")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(TestUtil.validUserRoleUpdateRequest())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andReturn();

        UserResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class);

        assertEquals(2L, response.id());
        assertTrue(response.roles().contains("ADMIN"));
    }
}
