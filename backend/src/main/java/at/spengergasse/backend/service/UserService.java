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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;

import static java.lang.Float.isNaN;

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
                .map(UserDto::fromEntityWithRoleNames)
                .toList();
    }

    public ResponseEntity<String> loginUser(String email, String password) {
        //Check if user exists in database
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if(optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login, email wrong");
        }

        //Get actual user from optional
        User user = optionalUser.get();

        if(!checkPassword(password, user.getSalt(), user.getHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login, password wrong");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Login successful!");
    }

    public ResponseEntity<?> addUser(UserRequest userRequest) {
        //validate user input
        if (userRequest == null || userRequest.firstname() == null || userRequest.lastname() == null ||
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
            String salt = generateSalt();
            String hash = calculateHash(userRequest.password(), salt);
            User user = User.builder()
                    .firstname(userRequest.firstname().toUpperCase().charAt(0) +
                            userRequest.firstname().substring(1).toLowerCase())
                    .lastname(userRequest.lastname().toUpperCase().charAt(0) +
                            userRequest.lastname().substring(1).toLowerCase())
                    .email(userRequest.email())
                    .salt(salt)
                    .hash(hash)
                    .colorNumber((int) (Math.random() * 7))
                    .build();

            for(String roleId : userRequest.roles()) {
                try {
                    Long.parseLong(roleId);
                } catch(NumberFormatException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Role ID must be a number.");
                }
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

        try {
            List<UserToRoles> rolesToDelete = new ArrayList<>();
            for(UserToRoles role : user.getRoles()) {
                if(!userDto.roles().contains(Objects.requireNonNull(role.getRole().getId()).toString())) {
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

    private static boolean checkPassword(String password, String salt, String hash) {
        return Objects.equals(hash, calculateHash(password, salt));
    }

    private static String generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[128 / 8];
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private static String calculateHash(String password, String salt) {
        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            byte[] passwordBytes = password.getBytes();

            Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(saltBytes, "HmacSHA256");
            hmacSHA256.init(secretKeySpec);

            String hashPassword = Base64.getEncoder().encodeToString(hmacSHA256.doFinal(passwordBytes));
            System.out.println("salt: " + salt);
            System.out.println("hash: " + hashPassword);
            System.out.println("hash: " + hashPassword);
            return hashPassword;
        } catch(NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }
}
