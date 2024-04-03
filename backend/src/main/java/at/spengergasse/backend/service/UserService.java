package at.spengergasse.backend.service;

import at.spengergasse.backend.dto.UserDto;
import at.spengergasse.backend.dto.UserRequest;
import at.spengergasse.backend.model.*;
import at.spengergasse.backend.persistence.RoleRepository;
import at.spengergasse.backend.persistence.UserRepository;
import at.spengergasse.backend.persistence.UserToRolesRepository;
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
    private final UserToRolesRepository userToRolesRepository;

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

    private boolean checkPassword(User user, String password) {
        return user.getPassword().equals(password);
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
                    .firstname(userRequest.firstname().toUpperCase().charAt(0) +
                            userRequest.firstname().substring(1).toLowerCase())
                    .lastname(userRequest.lastname().toUpperCase().charAt(0) +
                            userRequest.lastname().substring(1).toLowerCase())
                    .email(userRequest.email())
                    .password(userRequest.password())
                    .created(LocalDate.now())
                    .deleted(false)
                    .colorNumber((int) (Math.random() * 7))
                    .build();

            for(String roleId : userRequest.roles()) {
                Role role = roleRepository.findById(Long.parseLong(roleId));

                if (role == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Role " + roleId + " does not exist.");
                }

                UserToRoles userToRoles = UserToRoles.builder()
                        .id(new UserToRolesId(user.getId(), role.getId()))
                        .user(user)
                        .role(role)
                        .build();

                user.addRole(userToRoles);
            }

            userRepository.save(user);
            return ResponseEntity.ok(UserDto.fromEntity(user));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("An error occurred while adding user: " + e.getMessage());
        }
    }

    public ResponseEntity<?> editUser(UserDto userDto, String email) {
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User data is required.");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User user = userOptional.get();

        if(!Objects.equals(userDto.email(), user.getEmail())
                && userRepository.findByEmail(userDto.email()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists.");
        }

        user.setFirstname(userDto.firstname());
        user.setLastname(userDto.lastname());
        user.setEmail(userDto.email());
        user.setPassword(userDto.password());

        try {
            List<UserToRoles> rolesToDelete = new ArrayList<>();
            for(UserToRoles role : user.getRoles()) {
                if(userDto.roles().contains(Objects.requireNonNull(role.getRole().getId()).toString())) {
                    rolesToDelete.add(role);
                }
            }

            for(UserToRoles role : rolesToDelete) {
                user.removeRole(role.getRole());
                role.getRole().removeUser(user);
                userToRolesRepository.deleteById(role.getId());
            }

            for(String roleId : userDto.roles()) {
                Role role = roleRepository.findById(Long.parseLong(roleId));
                if (role == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Role " + roleId + " does not exist.");
                }
                if(!user.containsRole(role)) {
                    UserToRoles userToRoles = UserToRoles.builder()
                            .id(new UserToRolesId(user.getId(), role.getId()))
                            .user(user)
                            .role(role)
                            .build();
                    user.addRole(userToRoles);
                    userToRolesRepository.save(userToRoles);
                }
            }

            userRepository.save(user);
            return ResponseEntity.ok("User updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the user.");
        }
    }

    public ResponseEntity<?> deleteUser(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User user = userOptional.get();
        user.setDeletedDate(LocalDate.now());
        user.setDeleted(true);
        userRepository.save(user);

        return ResponseEntity.ok("User deleted successfully");
    }
}
