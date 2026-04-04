package org.shevchenko.taskmanagementspringapp.util;

import java.time.LocalDate;
import java.util.Set;
import org.shevchenko.taskmanagementspringapp.dto.comment.CommentCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.label.AssignLabelsToTaskRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.label.LabelCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.project.ProjectCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.task.TaskUpdateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserLoginRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserRegistrationRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserUpdateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.user.UserUpdateRoleRequestDto;
import org.shevchenko.taskmanagementspringapp.model.Task;

public class TestUtil {
    public static final String ADMIN_EMAIL = "admin@example.com";
    public static final String USER_EMAIL = "user@example.com";
    public static final String ANOTHER_USER_EMAIL = "another@example.com";
    public static final String PASSWORD = "Password1!";

    private TestUtil() {
    }

    public static UserRegistrationRequestDto validRegistrationRequest() {
        UserRegistrationRequestDto dto = new UserRegistrationRequestDto();
        dto.setEmail("new.user@example.com");
        dto.setPassword(PASSWORD);
        dto.setRepeatPassword(PASSWORD);
        dto.setUsername("newuser");
        dto.setFirstName("New");
        dto.setLastName("User");
        return dto;
    }

    public static UserRegistrationRequestDto invalidRegistrationRequest() {
        UserRegistrationRequestDto dto = new UserRegistrationRequestDto();
        dto.setEmail("bad-email");
        dto.setPassword("weak");
        dto.setRepeatPassword("different");
        dto.setUsername("");
        dto.setFirstName("");
        dto.setLastName("");
        return dto;
    }

    public static UserLoginRequestDto validLoginRequest() {
        return new UserLoginRequestDto(USER_EMAIL, PASSWORD);
    }

    public static UserLoginRequestDto invalidLoginRequest() {
        return new UserLoginRequestDto("bad-email", "123");
    }

    public static CommentCreateRequestDto validCommentRequest() {
        return new CommentCreateRequestDto(1L, "This is a test comment");
    }

    public static CommentCreateRequestDto invalidCommentRequest() {
        return new CommentCreateRequestDto(null, " ");
    }

    public static LabelCreateRequestDto validLabelRequest() {
        return new LabelCreateRequestDto("Bug", "#FF0000");
    }

    public static LabelCreateRequestDto invalidLabelRequest() {
        return new LabelCreateRequestDto(" ", " ");
    }

    public static ProjectCreateRequestDto validProjectRequest() {
        ProjectCreateRequestDto dto = new ProjectCreateRequestDto();
        dto.setName("New Project");
        dto.setDescription("Project description");
        return dto;
    }

    public static ProjectCreateRequestDto updatedProjectRequest() {
        ProjectCreateRequestDto dto = new ProjectCreateRequestDto();
        dto.setName("Updated Project");
        dto.setDescription("Updated description");
        return dto;
    }

    public static TaskCreateRequestDto validTaskCreateRequest() {
        return new TaskCreateRequestDto(
                "New Task",
                Task.Priority.HIGH,
                Task.Status.NOT_STARTED,
                LocalDate.now().plusDays(7)
        );
    }

    public static TaskCreateRequestDto invalidTaskCreateRequest() {
        return new TaskCreateRequestDto(
                " ",
                null,
                null,
                null
        );
    }

    public static TaskUpdateRequestDto validTaskUpdateRequest() {
        return new TaskUpdateRequestDto(
                "Updated Task",
                Task.Priority.MEDIUM,
                Task.Status.IN_PROGRESS,
                LocalDate.now().plusDays(10)
        );
    }

    public static AssignLabelsToTaskRequestDto validAssignLabelsRequest() {
        return new AssignLabelsToTaskRequestDto(Set.of(1L));
    }

    public static UserUpdateRequestDto validUserUpdateRequest() {
        return new UserUpdateRequestDto(
                "updated.user@example.com",
                "updateduser",
                "Updated",
                "Name"
        );
    }

    public static UserUpdateRoleRequestDto validUserRoleUpdateRequest() {
        return new UserUpdateRoleRequestDto("ADMIN");
    }
}
