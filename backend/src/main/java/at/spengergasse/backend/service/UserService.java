package at.spengergasse.backend.service;

import at.spengergasse.backend.dto.UserDto;
import at.spengergasse.backend.model.ERoles;
import at.spengergasse.backend.model.User;
import at.spengergasse.backend.model.UserToRoles;
import at.spengergasse.backend.model.UserToRolesId;
import at.spengergasse.backend.persistence.RoleRepository;
import at.spengergasse.backend.persistence.UserRepository;
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
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if(optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Login, Email wrong");
        }

        User user = optionalUser.get();

        if(!checkPassword(user, password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Login, Password wrong");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Login Successful!");
    }

    //TODO: Test method before merging into main!!!
    public ResponseEntity<?> addUser(UserDto userDto) {
        User user = UserDto.toEntity(userDto);

        List<UserToRoles> userToRoles = new ArrayList<>();
        userDto.roles().forEach(role -> {
            userToRoles.add(UserToRoles.builder()
                    .id(new UserToRolesId(user.getId(), roleRepository.findByRoleName(ERoles.valueOf(role)).getId()))
                    .user(user)
                    .role(roleRepository.findByRoleName(ERoles.valueOf(role)))
                    .build());
        });

        user.setRoles(userToRoles);

        userRepository.save(user);
        return ResponseEntity.ok("User added");
    }

    private boolean checkPassword(User user, String password) {
        return user.getPassword().equals(password);
    }


}
