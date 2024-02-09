package at.spengergasse.backend.persistence;

import at.spengergasse.backend.model.User;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends Repository<User, UUID> {
    User save(User user);
    User findById(UUID id);
    Optional<User> findByEmail(String email);
    User findByLastname(String lastname);
    List<User> findAll();
    void delete(User user); //for testing purposes
}
