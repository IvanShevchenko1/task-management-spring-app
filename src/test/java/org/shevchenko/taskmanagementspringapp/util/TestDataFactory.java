package org.shevchenko.taskmanagementspringapp.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.shevchenko.taskmanagementspringapp.dto.comment.CommentCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.comment.CommentResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.label.LabelCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.label.LabelResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.project.ProjectCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.project.ProjectResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskUpdateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserRegistrationRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserResponseDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserUpdateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserUpdateRoleRequestDto;
import org.shevchenko.taskmanagementspringapp.model.Comment;
import org.shevchenko.taskmanagementspringapp.model.Label;
import org.shevchenko.taskmanagementspringapp.model.Project;
import org.shevchenko.taskmanagementspringapp.model.Role;
import org.shevchenko.taskmanagementspringapp.model.Task;
import org.shevchenko.taskmanagementspringapp.model.User;

public final class TestDataFactory {
    private TestDataFactory() {
    }

    public static TaskCreateRequestDto taskCreateRequest() {
        return new TaskCreateRequestDto(
                "Implement tests",
                Task.Priority.HIGH,
                Task.Status.NOT_STARTED,
                LocalDate.of(2026, 4, 10)
        );
    }

    public static TaskUpdateRequestDto taskUpdateRequest() {
        return new TaskUpdateRequestDto(
                "Implement integration tests",
                Task.Priority.MEDIUM,
                Task.Status.IN_PROGRESS,
                LocalDate.of(2026, 4, 15)
        );
    }

    public static TaskResponseDto taskResponse(Long id, Long projectId) {
        return new TaskResponseDto(
                id,
                "Implement tests",
                Task.Priority.HIGH,
                Task.Status.NOT_STARTED,
                LocalDate.of(2026, 4, 10),
                projectId
        );
    }

    public static ProjectCreateRequestDto projectCreateRequest() {
        ProjectCreateRequestDto dto = new ProjectCreateRequestDto();
        dto.setName("Task Management API");
        dto.setDescription("Spring Boot course project");
        return dto;
    }

    public static ProjectResponseDto projectResponse(Long id, Long ownerId) {
        return new ProjectResponseDto(
                id,
                "Task Management API",
                "Spring Boot course project",
                Project.Status.IN_PROGRESS,
                LocalDateTime.of(2026, 4, 2, 10, 30),
                ownerId
        );
    }

    public static CommentCreateRequestDto commentCreateRequest(Long taskId) {
        return new CommentCreateRequestDto(taskId, "Looks good");
    }

    public static CommentResponseDto commentResponse(Long id, Long taskId, Long userId) {
        return new CommentResponseDto(id, taskId, userId, "Looks good", LocalDateTime.of(2026, 4, 2, 10, 15));
    }

    public static LabelCreateRequestDto labelCreateRequest() {
        return new LabelCreateRequestDto("Backend", "#2563EB");
    }

    public static LabelResponseDto labelResponse(Long id) {
        return new LabelResponseDto(id, "Backend", "#2563EB");
    }

    public static UserRegistrationRequestDto userRegistrationRequest() {
        UserRegistrationRequestDto dto = new UserRegistrationRequestDto();
        dto.setEmail("john@example.com");
        dto.setPassword("Password1!");
        dto.setRepeatPassword("Password1!");
        dto.setUsername("johnny");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        return dto;
    }

    public static UserResponseDto userResponse(Long id) {
        return new UserResponseDto(
                id,
                "john@example.com",
                "johnny",
                "John",
                "Doe",
                Set.of("USER")
        );
    }

    public static UserUpdateRequestDto userUpdateRequest() {
        return new UserUpdateRequestDto("updated@example.com", "updatedUser", "Jane", "Roe");
    }

    public static UserUpdateRoleRequestDto userUpdateRoleRequest(String role) {
        return new UserUpdateRoleRequestDto(role);
    }

    public static Project project(Long id, User owner) {
        Project project = new Project();
        project.setId(id);
        project.setName("Task Management API");
        project.setDescription("Spring Boot course project");
        project.setStartDate(LocalDate.of(2026, 4, 2));
        project.setStatus(Project.Status.IN_PROGRESS);
        project.setOwner(owner);
        return project;
    }

    public static Task task(Long id, Project project) {
        Task task = new Task();
        task.setId(id);
        task.setName("Implement tests");
        task.setPriority(Task.Priority.HIGH);
        task.setStatus(Task.Status.NOT_STARTED);
        task.setDueDate(LocalDate.of(2026, 4, 10));
        task.setProject(project);
        return task;
    }

    public static Comment comment(Long id, Task task, User user) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setTask(task);
        comment.setUser(user);
        comment.setText("Looks good");
        comment.setTimestamp(LocalDateTime.of(2026, 4, 2, 10, 15));
        return comment;
    }

    public static Label label(Long id, User user) {
        Label label = new Label();
        label.setId(id);
        label.setName("Backend");
        label.setColor("#2563EB");
        label.setUser(user);
        return label;
    }

    public static Role role(Role.RoleName roleName) {
        Role role = new Role();
        role.setId(roleName == Role.RoleName.ADMIN ? 1L : 2L);
        role.setRole(roleName);
        return role;
    }

    public static User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("john@example.com");
        user.setUsername("johnny");
        user.setPassword("encoded-password");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRoles(new HashSet<>());
        return user;
    }

    public static User admin(Long id) {
        User user = user(id);
        user.getRoles().add(role(Role.RoleName.ADMIN));
        return user;
    }

    public static User standardUser(Long id) {
        User user = user(id);
        user.getRoles().add(role(Role.RoleName.USER));
        return user;
    }
}
