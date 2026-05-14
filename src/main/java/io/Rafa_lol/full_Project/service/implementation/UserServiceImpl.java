package io.Rafa_lol.full_Project.service.implementation;


import io.Rafa_lol.full_Project.domain.User;
import io.Rafa_lol.full_Project.dto.UserDTO;
import io.Rafa_lol.full_Project.dtomapper.UserDTOMapper;
import io.Rafa_lol.full_Project.repository.UserRepository;
import io.Rafa_lol.full_Project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;

    @Override
    public UserDTO createUser(User user) {
        return UserDTOMapper.fromUser(userRepository.create(user));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return UserDTOMapper.fromUser(userRepository.getUserByEmail(email));
    }

    @Override
    public void sendVerificationCode(UserDTO user) {
        userRepository.sendVerificationCode(user);
    }


}
