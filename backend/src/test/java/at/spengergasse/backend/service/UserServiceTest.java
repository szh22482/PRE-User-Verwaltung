package at.spengergasse.backend.service;

import at.spengergasse.backend.dto.UserDto;
import at.spengergasse.backend.dto.UserRequest;
import at.spengergasse.backend.model.User;
import at.spengergasse.backend.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void verifySaveValidUser() {
        Optional<User> optUser = userRepository.findByEmail("save.valid@user.com");
        optUser.ifPresent(value -> userRepository.delete(value));

        UserRequest userRequest = UserRequest.builder()
                .firstname("Save")
                .lastname("User")
                .email("save.valid@user.com")
                .password("password")
                .roles(List.of("ADMINISTRATOR", "AUDITOR"))
                .build();

        ResponseEntity<?> user = userService.addUser(userRequest);
        assertThat(user.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(user.getBody()).isInstanceOf(UserDto.class);
    }

    @Test
    void verifySaveInvalidUser() {
        UserRequest userRequest = UserRequest.builder()
                .firstname("Invalid")
                .lastname("")
                .email("invalid@user.com")
                .password("password")
                .roles(List.of("ADMINISTRATOR", "AUDITOR"))
                .build();

        ResponseEntity<?> user = userService.addUser(userRequest);
        assertThat(user.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        //assertThat(userRepository.findByEmail("invalid@user.com")).isEmpty();
    }

    @Test
    void verifySaveUserWithoutRoles() {
        UserRequest userRequest = UserRequest.builder()
                .firstname("No")
                .lastname("Roles")
                .email("no@roles.com")
                .password("password")
                .roles(null)
                .build();

        ResponseEntity<?> user = userService.addUser(userRequest);
        assertThat(userRepository.findByEmail("no@roles.com")).isEmpty();
        assertThat(user.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void verifySaveUserWithExistingMail() {
        Optional<User> optUser = userRepository.findByEmail("existing@mail.com");
        optUser.ifPresent(value -> userRepository.delete(value));

        UserRequest userRequest = UserRequest.builder()
                .firstname("Existing")
                .lastname("Mail")
                .email("existing@mail.com")
                .password("password")
                .roles(List.of("ADMINISTRATOR", "AUDITOR"))
                .build();

        ResponseEntity<?> user = userService.addUser(userRequest);
        assertThat(user.getStatusCode().is2xxSuccessful()).isTrue();
        ResponseEntity<?> user2 = userService.addUser(userRequest);
        assertThat(user2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
