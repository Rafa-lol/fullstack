package io.Rafa_lol.full_Project.resource;


import io.Rafa_lol.full_Project.domain.HttpResponse;
import io.Rafa_lol.full_Project.domain.User;
import io.Rafa_lol.full_Project.dto.UserDTO;
import io.Rafa_lol.full_Project.form.LoginForm;
import io.Rafa_lol.full_Project.repository.UserRepository;
import io.Rafa_lol.full_Project.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import static java.time.LocalTime.now;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
public class UserResource {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;


    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.getEmail(), loginForm.getPassword()));

        UserDTO userDTO = userService.getUserByEmail(loginForm.getEmail());

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(now().toString())
                        .data(Map.of("user", userDTO))
                        .message("Login Success")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());

    }



    @PostMapping("/register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user) {
        UserDTO userDTO= userService.createUser(user);
        return ResponseEntity.created(getUri()).body(
                HttpResponse.builder()
                        .timestamp(now().toString())
                        .data(Map.of("user", userDTO))
                        .message("User created")
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build());
    }

    private URI getUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentRequest().path("/user/get/<userId>" ).toUriString());
    }


}
