package at.spengergasse.backend.service;

import at.spengergasse.backend.dto.UserDto;
import at.spengergasse.backend.persistence.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public List<UserDto> fetchAllUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> !user.getDeleted())
                .map(UserDto::fromEntity)
                .toList();
    }
}
