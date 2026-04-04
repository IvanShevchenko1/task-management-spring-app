package org.shevchenko.taskmanagementspringapp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.shevchenko.taskmanagementspringapp.dto.comment.CommentResponseDto;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = true)
class CommentControllerTest {
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
    @DisplayName("GET /comments without authentication -> 401 Unauthorized")
    void getComments_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/comments").param("taskId", "1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /comments with USER authority -> 201 Created")
    @WithUserDetails(TestUtil.USER_EMAIL)
    @Sql(scripts = {
            "classpath:database/common/insert_test_users.sql",
            "classpath:database/common/insert_test_projects_tasks_labels_comments.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void addComment_WithUser_ReturnsCreated() throws Exception {
        MvcResult result = mockMvc.perform(post("/comments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(TestUtil.validCommentRequest())))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andReturn();

        CommentResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), CommentResponseDto.class);

        assertEquals(1L, response.taskId());
        assertEquals(2L, response.userId());
        assertEquals("This is a test comment", response.text());
    }

    @Test
    @DisplayName("POST /comments with invalid body -> 400 Bad Request")
    @WithUserDetails(TestUtil.USER_EMAIL)
    @Sql(scripts = "classpath:database/common/insert_test_users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void addComment_WithInvalidBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/comments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(TestUtil.invalidCommentRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /comments with USER authority -> 200 OK")
    @WithUserDetails(TestUtil.USER_EMAIL)
    @Sql(scripts = {
            "classpath:database/common/insert_test_users.sql",
            "classpath:database/common/insert_test_projects_tasks_labels_comments.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getComments_WithUser_ReturnsOk() throws Exception {
        MvcResult result = mockMvc.perform(get("/comments")
                        .param("taskId", "1")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode contentNode = root.get("content");

        assertTrue(contentNode.isArray());
        assertFalse(contentNode.isEmpty());
        assertEquals("Existing comment", contentNode.get(0).get("text").asText());
    }

    @Test
    @DisplayName("DELETE /comments/{id} by owner -> 204 No Content")
    @WithUserDetails(TestUtil.USER_EMAIL)
    @Sql(scripts = {
            "classpath:database/common/insert_test_users.sql",
            "classpath:database/common/insert_test_projects_tasks_labels_comments.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteComment_ByOwner_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/comments/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
