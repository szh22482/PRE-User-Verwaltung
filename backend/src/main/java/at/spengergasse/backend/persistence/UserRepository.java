package at.spengergasse.backend.persistence;

import at.spengergasse.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    User findByLastname(String lastname);
    List<User> findAll();
    void delete(User user); //for testing purposes
}
