package at.spengergasse.backend.service;

import at.spengergasse.backend.dto.UserDto;
import at.spengergasse.backend.model.User;
import at.spengergasse.backend.persistence.UserRepository;
import at.spengergasse.backend.persistence.UserToRolesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

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

    public ResponseEntity<?> editUser(UserDto userDto) {
        //validate user input
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User data is required.");
        }

        //check if user exists
        Optional<User> userOptional = userRepository.findByEmail(userDto.email());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User user = userOptional.get();

        //update user
        user.setFirstname(userDto.firstname());
        user.setLastname(userDto.lastname());
        user.setEmail(userDto.email());
        user.setPassword(userDto.password());

        //update roles
        List<UserToRoles> userRoles = new ArrayList<>();
        userToRolesRepository.deleteByUser(user);
        for (String roleName : userDto.roles()) {
            try {
                ERoles roleEnum = ERoles.valueOf(roleName);
                Role role = roleRepository.findByRoleName(roleEnum);

                UserToRoles userRole = UserToRoles.builder()
                        .id(new UserToRolesId(user.getId(), role.getId()))
                        .user(user)
                        .role(role)
                        .build();
                userRoles.add(userRole);
                userToRolesRepository.save(userRole);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Roles could not be updated.");
            }
        }
        user.setRoles(userRoles);

        userRepository.save(user);
        return ResponseEntity.ok("User updated successfully");
    }
}
