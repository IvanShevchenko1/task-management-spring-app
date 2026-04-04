package org.shevchenko.taskmanagementspringapp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.shevchenko.taskmanagementspringapp.dto.project.ProjectResponseDto;
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
class ProjectControllerTest {
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
    @DisplayName("GET /projects without authentication -> 401 Unauthorized")
    void getAll_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/projects"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /projects with USER authority -> 201 Created")
    @WithUserDetails(TestUtil.USER_EMAIL)
    @Sql(scripts = "classpath:database/common/insert_test_users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void create_WithUser_ReturnsCreated() throws Exception {
        MvcResult result = mockMvc.perform(post("/projects")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(TestUtil.validProjectRequest())))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andReturn();

        ProjectResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), ProjectResponseDto.class);

        assertEquals("New Project", response.name());
        assertEquals(2L, response.owner_id());
    }

    @Test
    @DisplayName("GET /projects with USER authority -> 200 OK")
    @WithUserDetails(TestUtil.USER_EMAIL)
    @Sql(scripts = {
            "classpath:database/common/insert_test_users.sql",
            "classpath:database/common/insert_test_projects_tasks_labels_comments.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAll_WithUser_ReturnsOnlyOwnedProjects() throws Exception {
        MvcResult result = mockMvc.perform(get("/projects")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode contentNode = root.get("content");

        assertTrue(contentNode.isArray());
        assertFalse(contentNode.isEmpty());
        assertEquals(1, contentNode.size());
        assertEquals("User Project", contentNode.get(0).get("name").asText());
    }

    @Test
    @DisplayName("GET /projects/{id} with USER authority -> 200 OK")
    @WithUserDetails(TestUtil.USER_EMAIL)
    @Sql(scripts = {
            "classpath:database/common/insert_test_users.sql",
            "classpath:database/common/insert_test_projects_tasks_labels_comments.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getById_WithUser_ReturnsOk() throws Exception {
        MvcResult result = mockMvc.perform(get("/projects/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andReturn();

        ProjectResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), ProjectResponseDto.class);

        assertEquals(1L, response.id());
        assertEquals("User Project", response.name());
    }

    @Test
    @DisplayName("PUT /projects/{id} with USER authority -> 200 OK")
    @WithUserDetails(TestUtil.USER_EMAIL)
    @Sql(scripts = {
            "classpath:database/common/insert_test_users.sql",
            "classpath:database/common/insert_test_projects_tasks_labels_comments.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void update_WithUser_ReturnsOk() throws Exception {
        MvcResult result = mockMvc.perform(put("/projects/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(TestUtil.updatedProjectRequest())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andReturn();

        ProjectResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), ProjectResponseDto.class);

        assertEquals(1L, response.id());
        assertEquals("Updated Project", response.name());
        assertEquals("Updated description", response.description());
    }

    @Test
    @DisplayName("DELETE /projects/{id} with USER authority -> 204 No Content")
    @WithUserDetails(TestUtil.USER_EMAIL)
    @Sql(scripts = {
            "classpath:database/common/insert_test_users.sql",
            "classpath:database/common/insert_test_projects_tasks_labels_comments.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void delete_WithUser_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/projects/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
