package org.shevchenko.taskmanagementspringapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.shevchenko.taskmanagementspringapp.constraint.FieldMatch;

@Data
@FieldMatch(firstField = "password",
        secondField = "repeatPassword",
        message = "Passwords must match")
public class UserRegistrationRequestDto {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 6, max = 20)
    private String password;
    @NotBlank
    @Size(min = 6, max = 20)
    private String repeatPassword;
    @NotBlank
    private String username;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
