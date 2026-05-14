package io.Rafa_lol.full_Project.service;

import io.Rafa_lol.full_Project.domain.User;
import io.Rafa_lol.full_Project.dto.UserDTO;
import jakarta.validation.constraints.NotEmpty;


public interface UserService {

    UserDTO createUser(User user);

    UserDTO getUserByEmail(String email);

    void sendVerificationCode(UserDTO user);

}
