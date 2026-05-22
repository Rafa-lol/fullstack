package io.Rafa_lol.full_Project.utils;

import io.Rafa_lol.full_Project.domain.UserPrincipal;
import io.Rafa_lol.full_Project.dto.UserDTO;
import org.springframework.security.core.Authentication;

public class UserUtils {

    public static UserDTO getAuthenticatedUser(Authentication authentication) {
        return (UserDTO) authentication.getPrincipal();
    }


    public static UserDTO getLoggedInUser(Authentication authentication) {
        return ((UserPrincipal) authentication.getPrincipal()).getUser();
    }
}
