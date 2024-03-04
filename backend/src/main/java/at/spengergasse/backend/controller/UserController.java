package at.spengergasse.backend.controller;

import at.spengergasse.backend.dto.UserDto;
import at.spengergasse.backend.persistence.UserRepository;
import at.spengergasse.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/api/users/")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserService userService;

    @GetMapping("/all")
    public List<UserDto> listAll() {
        return userService.fetchAllUsers();
    }

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        return userService.loginUser(email, password);
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editUser(@RequestBody UserDto userDto, @RequestParam String email) {
        return userService.editUser(userDto, email);
    }
}
