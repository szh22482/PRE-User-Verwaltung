package at.spengergasse.backend.controller;

import at.spengergasse.backend.dto.UserDto;
import at.spengergasse.backend.persistence.UserRepository;
import at.spengergasse.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:3000","http://127.0.0.1:3000"})
public class UserController {
    private final UserService userService;

    @GetMapping("/all")
    public List<UserDto> listAll() {
        return userService.fetchAllUsers();
    }
}
