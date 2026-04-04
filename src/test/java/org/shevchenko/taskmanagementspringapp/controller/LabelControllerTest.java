package org.shevchenko.taskmanagementspringapp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.shevchenko.taskmanagementspringapp.dto.label.LabelResponseDto;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = true)
class LabelControllerTest {
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
    @DisplayName("GET /labels without authentication -> 401 Unauthorized")
    void getAll_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/labels"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /labels with USER authority -> 201 Created")
    @WithUserDetails(TestUtil.USER_EMAIL)
    @Sql(scripts = "classpath:database/common/insert_test_users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void create_WithUser_ReturnsCreated() throws Exception {
        MvcResult result = mockMvc.perform(post("/labels")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(TestUtil.validLabelRequest())))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andReturn();

        LabelResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), LabelResponseDto.class);

        assertEquals("Bug", response.name());
        assertEquals("#FF0000", response.color());
    }

    @Test
    @DisplayName("POST /labels with invalid body -> 400 Bad Request")
    @WithUserDetails(TestUtil.USER_EMAIL)
    @Sql(scripts = "classpath:database/common/insert_test_users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void create_WithInvalidBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/labels")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(TestUtil.invalidLabelRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /labels with USER authority -> 200 OK")
    @WithUserDetails(TestUtil.USER_EMAIL)
    @Sql(scripts = {
            "classpath:database/common/insert_test_users.sql",
            "classpath:database/common/insert_test_projects_tasks_labels_comments.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAll_WithUser_ReturnsOk() throws Exception {
        MvcResult result = mockMvc.perform(get("/labels")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode contentNode = root.get("content");

        assertTrue(contentNode.isArray());
        assertFalse(contentNode.isEmpty());
        assertEquals("Urgent", contentNode.get(0).get("name").asText());
    }

    @Test
    @DisplayName("PUT /labels/{id} with USER authority -> 200 OK")
    @WithUserDetails(TestUtil.USER_EMAIL)
    @Sql(scripts = {
            "classpath:database/common/insert_test_users.sql",
            "classpath:database/common/insert_test_projects_tasks_labels_comments.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void update_WithUser_ReturnsOk() throws Exception {
        MvcResult result = mockMvc.perform(put("/labels/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(TestUtil.validLabelRequest())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andReturn();

        LabelResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), LabelResponseDto.class);

        assertEquals(1L, response.id());
        assertEquals("Bug", response.name());
    }

    @Test
    @DisplayName("DELETE /labels/{id} with USER authority -> 204 No Content")
    @WithUserDetails(TestUtil.USER_EMAIL)
    @Sql(scripts = {
            "classpath:database/common/insert_test_users.sql",
            "classpath:database/common/insert_test_projects_tasks_labels_comments.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void delete_WithUser_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/labels/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
