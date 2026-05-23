package io.Rafa_lol.full_Project.service;

import io.Rafa_lol.full_Project.domain.User;
import io.Rafa_lol.full_Project.dto.UserDTO;
import io.Rafa_lol.full_Project.form.UpdateForm;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {

    UserDTO createUser(User user);

    UserDTO getUserByEmail(String email);

    void sendVerificationCode(UserDTO user);



    UserDTO verifyCode(String email, String code);

    void resetPassword(String email);

    UserDTO verifyPasswordKey(String key);

    void renewPassword(String key, String password, String confirmPassword);

    UserDTO verifyAccountKey(String key);

    UserDTO updateUserDetails(@Valid UpdateForm user);

    UserDTO getUserById(Long userId);

    void updatePassword(Long userId, @NotEmpty(message = "Current Password cannot be empty") String currentPassword, @NotEmpty(message = "New Password cannot be empty") String newPassword, @NotEmpty(message = "Confirm Password cannot be empty") String confirmNewPassword);

    void updateUserRole(Long userId, String roleName);

    void updateAccountSettings(Long id, @NotNull(message = "Enable cannot be null or empty") Boolean enabled, @NotNull(message = "Not Locked cannot be null or empty") Boolean notLocked);

    UserDTO toggleMfa(String email);

    void updateImage(UserDTO user, MultipartFile image);
}
