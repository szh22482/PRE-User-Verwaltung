package at.spengergasse.backend.service;

import at.spengergasse.backend.dto.UserDto;
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

    public ResponseEntity<?> editUser(UserDto userDto) {
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User data is required.");
        }

        Optional<User> userOptional = userRepository.findByEmail(userDto.email());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User user = userOptional.get();

        user.setFirstname(userDto.firstname());
        user.setLastname(userDto.lastname());
        user.setEmail(userDto.email());
        user.setPassword(userDto.password());

        try {
            List<UserToRoles> findRoles = userToRolesRepository.findByUser(user);
            List<UserToRoles> roles = new ArrayList<>();
            for(String roleId: userDto.roles()) {
                try {
                    Long roleIdLong = Long.parseLong(roleId);
                    Role role = roleRepository.findById(roleIdLong);
                    if(role != null) {
                        UserToRoles userRole = UserToRoles.builder()
                                .id(new UserToRolesId(user.getId(), role.getId()))
                                .user(user)
                                .role(role)
                                .build();
                        roles.add(userRole);
                    }
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("An error occurred while updating the user.");
                }
            }

            for(UserToRoles deleteRole: findRoles) {
                boolean found = false;
                for(UserToRoles role: roles) {
                    if (Objects.equals(role.getRole().getId(), deleteRole.getRole().getId())) {
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    userToRolesRepository.delete(deleteRole);
                }
            }

            for(UserToRoles role: roles) {
                if(!findRoles.contains(role)) userToRolesRepository.save(role);
            }

            user.setRoles(roles);
            userRepository.save(user);

            return ResponseEntity.ok("User updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the user.");
        }
    }
}
