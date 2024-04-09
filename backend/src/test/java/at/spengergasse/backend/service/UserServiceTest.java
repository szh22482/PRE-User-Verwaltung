package at.spengergasse.backend.service;

import at.spengergasse.backend.dto.UserDto;
import at.spengergasse.backend.dto.UserRequest;
import at.spengergasse.backend.model.*;
import at.spengergasse.backend.persistence.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;

    private static UserDto userDto;
    private static UserRequest userRequest;
    private static final UUID uuid = UUID.randomUUID();

    @BeforeAll
    public static void createUser() {
        User user = User.builder()
                .firstname("Max")
                .lastname("Mustermann")
                .email("max@mustermann.com")
                .colorNumber(4)
                .password("pass123")
                .roles(new ArrayList<>())
                .build();

        userRequest = UserRequest.builder()
                .firstname("Max")
                .lastname("Mustermann")
                .email("max@mustermann.com")
                .password("pass123")
                .roles(new ArrayList<>(List.of("4")))
                .build();

        user.addRole( UserToRoles.builder()
                .id(new UserToRolesId(uuid, 1L))
                .user(user)
                .role(Role.builder()
                        .roleName(ERoles.ADMINISTRATOR)
                        .build())
                .build());

        userDto = UserDto.fromEntity(user);
    }

    @Test
    void verifyFetchAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(UserDto.toEntity(userDto)));

        List<UserDto> users = userService.fetchAllUsers();
        assertThat(users).isNotEmpty();
        assertThat(users).hasSize(1);
        assertThat(users.getFirst().firstname()).isEqualTo(userDto.firstname());
    }

    @Test
    void verifyFetchAllUsersEmpty() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> users = userService.fetchAllUsers();
        assertThat(users).isEmpty();
    }

    @Test
    void verifyLoginUser() {
        when(userRepository.findByEmail("max@mustermann.com")).thenReturn(Optional.ofNullable(UserDto.toEntity(userDto)));
        ResponseEntity<?> response = userService.loginUser("max@mustermann.com", "pass123");

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("Login successful!");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void verifyLoginUserInvalidPassword() {
        when(userRepository.findByEmail("max@mustermann.com")).thenReturn(Optional.ofNullable(UserDto.toEntity(userDto)));
        ResponseEntity<?> response = userService.loginUser("max@mustermann.com", "wrong");

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("Invalid login, password wrong");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void verifyLoginUserInvalidEmail() {
        when(userRepository.findByEmail("wrong@email.com")).thenReturn(Optional.empty());
        ResponseEntity<?> response = userService.loginUser("wrong@email.com", "pass123");

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("Invalid login, email wrong");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void verifyLoginUserInvalidEmailAndPassword() {
        when(userRepository.findByEmail("wrong@email.com")).thenReturn(Optional.empty());
        ResponseEntity<?> response = userService.loginUser("wrong@email.com", "wrong");

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("Invalid login, email wrong");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void verifyAddUser() {
        when(userRepository.findByEmail("max@mustermann.com")).thenReturn(Optional.empty());
        when(userRepository.save(UserDto.toEntity(userDto))).thenReturn(UserDto.toEntity(userDto));

        ResponseEntity<?> response = userService.addUser(userRequest);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isInstanceOf(UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void verifyAddUserAlreadyExists() {
        when(userRepository.findByEmail("max@mustermann.com")).thenReturn(Optional.ofNullable(UserDto.toEntity(userDto)));

        ResponseEntity<?> response = userService.addUser(userRequest);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("User with email max@mustermann.com already exists.");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void verifyAddUserMissingFields() {
        UserRequest userRequest = UserRequest.builder()
                .firstname("Max")
                .lastname("Mustermann")
                .password("pass123")
                .roles(new ArrayList<>(List.of("4")))
                .build();

        ResponseEntity<?> response = userService.addUser(userRequest);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("Firstname, lastname, email and password are required.");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void verifyAddUserMissingRoles() {
        UserRequest userRequest = UserRequest.builder()
                .firstname("Max")
                .lastname("Mustermann")
                .email("max@mustermann.com")
                .password("pass123")
                .build();

        ResponseEntity<?> response = userService.addUser(userRequest);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("At least one role is required.");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void verifyAddUserInvalidRoleNumber() {
        UserRequest userRequest = UserRequest.builder()
                .firstname("Max")
                .lastname("Mustermann")
                .email("max@mustermann.com")
                .password("pass123")
                .roles(new ArrayList<>(List.of("7")))
                .build();

        ResponseEntity<?> response = userService.addUser(userRequest);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("Role 7 does not exist.");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void verifyAddUserInvalidRoleName() {
        UserRequest userRequest = UserRequest.builder()
                .firstname("Max")
                .lastname("Mustermann")
                .email("max@mustermann.com")
                .password("pass123")
                .roles(new ArrayList<>(List.of("Gast")))
                .build();

        ResponseEntity<?> response = userService.addUser(userRequest);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("Role ID must be a number.");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void verifyAddUserNull() {
        ResponseEntity<?> response = userService.addUser(null);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("Firstname, lastname, email and password are required.");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void verifyEditUserDtoIsNull() {
        ResponseEntity<?> response = userService.editUser(null, "max@mustermann.com");

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("User data is required.");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void verifyEditUserEmailIsNull() {
        ResponseEntity<?> response = userService.editUser(userDto, null);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("User not found.");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void verifyEditUserEmailNotFound() {
        when(userRepository.findByEmail("max@mustermann.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = userService.editUser(userDto, "max@mustermann.com");

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("User not found.");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void verifyEditUserEmailAlreadyExists() {
        when(userRepository.findByEmail("max@mustermann.com")).thenReturn(Optional.ofNullable(UserDto.toEntity(userDto)));

        User user1 = User.builder()
                .firstname("Max")
                .lastname("Mustermann")
                .email("max1@mustermann.com")
                .colorNumber(4)
                .password("pass123")
                .roles(new ArrayList<>())
                .build();

        UserDto userDto1 = UserDto.fromEntity(user1);
        when(userRepository.findByEmail("max1@mustermann.com")).thenReturn(Optional.ofNullable(UserDto.toEntity(userDto1)));

        ResponseEntity<?> response = userService.editUser(userDto1, "max@mustermann.com");

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("Email already exists.");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void verifyEditUser() {
        when(userRepository.findByEmail("max@mustermann.com")).thenReturn(Optional.ofNullable(UserDto.toEntity(userDto)));
        when(userRepository.save(UserDto.toEntity(userDto))).thenReturn(UserDto.toEntity(userDto));

        User user1 = User.builder()
                .firstname("Max")
                .lastname("Mustermann")
                .email("max1@mustermann.com")
                .colorNumber(4)
                .password("pass123")
                .roles(new ArrayList<>())
                .build();

        user1.addRole( UserToRoles.builder()
                .id(new UserToRolesId(uuid, 1L))
                .user(user1)
                .role(Role.builder()
                        .roleName(ERoles.ADMINISTRATOR)
                        .build())
                .build());

        UserDto userDto1 = UserDto.fromEntity(user1);

        ResponseEntity<?> response = userService.editUser(userDto1, "max@mustermann.com");

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("User updated successfully");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void verifyEditUserNoChange() {
        when(userRepository.findByEmail("max@mustermann.com")).thenReturn(Optional.ofNullable(UserDto.toEntity(userDto)));
        when(userRepository.save(UserDto.toEntity(userDto))).thenReturn(UserDto.toEntity(userDto));

        ResponseEntity<?> response = userService.editUser(userDto, "max@mustermann.com");

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("User updated successfully");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void verifyEditUserNewRole() {
        when(userRepository.findByEmail("max@mustermann.com")).thenReturn(Optional.ofNullable(UserDto.toEntity(userDto)));
        when(userRepository.save(UserDto.toEntity(userDto))).thenReturn(UserDto.toEntity(userDto));

        User user1 = User.builder()
                .firstname("Max")
                .lastname("Mustermann")
                .email("max1@mustermann.com")
                .colorNumber(4)
                .password("pass123")
                .roles(new ArrayList<>())
                .build();

        user1.addRole(UserToRoles.builder()
                .id(new UserToRolesId(uuid, 1L))
                .user(user1)
                .role(Role.builder()
                        .roleName(ERoles.ADMINISTRATOR)
                        .build())
                .build());

        user1.addRole(UserToRoles.builder()
                .id(new UserToRolesId(uuid, 1L))
                .user(user1)
                .role(Role.builder()
                        .roleName(ERoles.AUDITOR)
                        .build())
                .build());

        UserDto userDto1 = UserDto.fromEntity(user1);

        ResponseEntity<?> response = userService.editUser(userDto1, "max@mustermann.com");

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("User updated successfully");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void verifyEditUserRemoveRole() {
        when(userRepository.findByEmail("max@mustermann.com")).thenReturn(Optional.ofNullable(UserDto.toEntity(userDto)));
        when(userRepository.save(UserDto.toEntity(userDto))).thenReturn(UserDto.toEntity(userDto));

        User user1 = User.builder()
                .firstname("Max")
                .lastname("Mustermann")
                .email("max1@mustermann.com")
                .colorNumber(4)
                .password("pass123")
                .roles(new ArrayList<>())
                .build();

        user1.addRole(UserToRoles.builder()
                .id(new UserToRolesId(uuid, 1L))
                .user(user1)
                .role(Role.builder()
                        .roleName(ERoles.GAST)
                        .build())
                .build());

        user1.addRole(UserToRoles.builder()
                .id(new UserToRolesId(uuid, 1L))
                .user(user1)
                .role(Role.builder()
                        .roleName(ERoles.AUDITOR)
                        .build())
                .build());

        UserDto userDto1 = UserDto.fromEntity(user1);

        ResponseEntity<?> response = userService.editUser(userDto1, "max@mustermann.com");

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("User updated successfully");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void verifyDeleteUser() {
        when(userRepository.findByEmail("max@mustermann.com")).thenReturn(Optional.ofNullable(UserDto.toEntity(userDto)));

        ResponseEntity<?> response = userService.deleteUser("max@mustermann.com");

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("User deleted successfully");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void verifyDeleteUserNotFound() {
        when(userRepository.findByEmail("max@mustermann.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = userService.deleteUser("max@mustermann.com");

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("User not found.");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
