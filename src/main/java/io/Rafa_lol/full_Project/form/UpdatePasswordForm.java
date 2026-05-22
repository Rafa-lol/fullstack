package io.Rafa_lol.full_Project.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UpdatePasswordForm {

    @NotEmpty(message = "Current Password cannot be empty")
    private String currentPassword;
    @NotEmpty(message = "New Password cannot be empty")
    private String newPassword;
    @NotEmpty(message = "Confirm Password cannot be empty")
    private String confirmNewPassword;

}
