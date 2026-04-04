package org.shevchenko.taskmanagementspringapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.shevchenko.taskmanagementspringapp.dto.user.UserLoginResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserResponseDto;
import org.shevchenko.taskmanagementspringapp.util.TestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = true)
class AuthenticationControllerTest {
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
    @DisplayName("POST /auth/registration with valid body -> 201 Created")
    void register_WithValidRequest_ReturnsCreated() throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/registration")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                TestUtil.validRegistrationRequest())))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andReturn();

        UserResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class);

        assertFalse(response.roles().isEmpty());
        assertTrue(response.roles().contains("USER"));
    }

    @Test
    @DisplayName("POST /auth/registration with duplicate email -> 409 Conflict")
    @Sql(scripts = "classpath:database/common/insert_test_users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void register_WithDuplicateEmail_ReturnsConflict() throws Exception {
        var request = TestUtil.validRegistrationRequest();
        request.setEmail(TestUtil.USER_EMAIL);
        request.setUsername("duplicateuser");

        mockMvc.perform(post("/auth/registration")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /auth/registration with invalid body -> 400 Bad Request")
    void register_WithInvalidBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/auth/registration")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                TestUtil.invalidRegistrationRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/login with valid credentials -> 200 OK")
    @Sql(scripts = "classpath:database/common/insert_test_users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void login_WithValidCredentials_ReturnsToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(TestUtil.validLoginRequest())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andReturn();

        UserLoginResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserLoginResponseDto.class);

        assertFalse(response.token().isBlank());
    }

    @Test
    @DisplayName("POST /auth/login with invalid body -> 400 Bad Request")
    void login_WithInvalidBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(TestUtil.invalidLoginRequest())))
                .andExpect(status().isBadRequest());
    }
}
