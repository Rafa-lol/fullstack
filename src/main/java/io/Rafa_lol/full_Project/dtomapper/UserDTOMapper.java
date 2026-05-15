package io.Rafa_lol.full_Project.dtomapper;


import io.Rafa_lol.full_Project.domain.Role;
import io.Rafa_lol.full_Project.domain.User;
import io.Rafa_lol.full_Project.dto.UserDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;


public class UserDTOMapper {
        /// Update futuro, melhorar utilizando MapStruct- @Mapper(componentModel = "spring") e novas dependencias Maven
    public static UserDTO fromUser(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    public static UserDTO fromUser(User user, Role role) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        userDTO.setRoleName(role.getName());
        userDTO.setPermissions(role.getPermission());
        return userDTO ;
    }


    public static User toUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        return user;
    }

}
