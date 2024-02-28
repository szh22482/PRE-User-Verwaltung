package at.spengergasse.backend.service;

import at.spengergasse.backend.dto.UserDto;
import at.spengergasse.backend.dto.UserRequest;
import at.spengergasse.backend.model.*;
import at.spengergasse.backend.persistence.RoleRepository;
import at.spengergasse.backend.persistence.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private static final HashMap<String, Long> roles = new HashMap<>(Map.of(
            "ADMINISTRATOR", 1L,
            "AUDITOR", 2L,
            "AUDITEE", 3L,
            "REPORTER", 4L,
            "GAST", 5L,
            "MANUAL_WRITER", 6L
    ));

    public List<UserDto> fetchAllUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> !user.getDeleted())
                .map(UserDto::fromEntity)
                .toList();
    }

    public ResponseEntity<String> loginUser(String email, String password) {
        //Check if user exists in database
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if(optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Login, Email wrong");
        }

        //Get actual user from optional
        User user = optionalUser.get();

        if(!checkPassword(user, password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Login, Password wrong");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Login Successful!");
    }

    public ResponseEntity<?> addUser(UserRequest userRequest) {
        //validate user input
        if (userRequest.firstname() == null || userRequest.lastname() == null ||
                userRequest.email() == null || userRequest.password() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Firstname, lastname, email and password are required.");
        }

        //check if user has at least on role
        if (userRequest.roles() == null || userRequest.roles().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("At least one role is required.");
        }

        //check if user already exists
        Optional<User> userOptional = userRepository.findByEmail(userRequest.email());
        if (userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User with email " + userRequest.email() + " already exists.");
        }

        try {
            User user = User.builder()
                    .firstname(userRequest.firstname())
                    .lastname(userRequest.lastname())
                    .email(userRequest.email())
                    .password(userRequest.password())
                    .created(LocalDate.now())
                    .deleted(false)
                    .colorNumber((int) (Math.random() * 10))
                    .build();

            List<UserToRoles> userToRoles = new ArrayList<>();
            for(String roleName: userRequest.roles()) {
                Long id = roles.get(roleName);
                Role role = roleRepository.findById(id);

                if (role == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Role " + roleName + " does not exist.");
                }

                userToRoles.add(UserToRoles.builder()
                        .id(new UserToRolesId(user.getId(), role.getId()))
                        .user(user)
                        .role(role)
                        .build());
            }

            user.setRoles(userToRoles);
            userRepository.save(user);
            return ResponseEntity.ok(UserDto.fromEntity(user));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("An error occurred while adding user: " + e.getMessage());
        }
    }

    private boolean checkPassword(User user, String password) {
        return user.getPassword().equals(password);
    }
}
