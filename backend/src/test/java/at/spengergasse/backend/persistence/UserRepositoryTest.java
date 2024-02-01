package at.spengergasse.backend.persistence;

import at.spengergasse.backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveUserAndFindById() {
        // Erstelle einen neuen Benutzer
        User user = User.builder()
                .firstname("Max")
                .lastname("Mustermann")
                .email("max.mustermann@example.com")
                .password("pass123")
                .deleted(false)
                .created(new java.sql.Date(System.currentTimeMillis()))
                .build();

        // Speichere den Benutzer in der Datenbank
        User savedUser = userRepository.save(user);

        assertNotNull(savedUser);

        // Suche den Benutzer nach der ID
        User retrievedUser = userRepository.findById(user.getId());

        // Überprüfe, ob der gespeicherte Benutzer korrekt abgerufen wurde
        assertNotNull(retrievedUser);
        assertEquals("Max", retrievedUser.getFirstname());
        assertEquals("Mustermann", retrievedUser.getLastname());
        assertEquals("max.mustermann@example.com", retrievedUser.getEmail());
        assertEquals(false, retrievedUser.getDeleted());
    }

    @Test
    public void testFindByEmail() {
        // Erstelle einen neuen Benutzer
        User user = User.builder()
                .firstname("Alice")
                .lastname("Doe")
                .email("alice.doe@example.com")
                .password("pass456")
                .deleted(false)
                .created(new java.sql.Date(System.currentTimeMillis()))
                .build();

        // Speichere den Benutzer in der Datenbank
        userRepository.save(user);

        // Suche den Benutzer nach der E-Mail-Adresse
        User retrievedUser = userRepository.findByEmail("alice.doe@example.com");

        // Überprüfe, ob der gespeicherte Benutzer korrekt abgerufen wurde
        assertNotNull(retrievedUser);
        assertEquals("Alice", retrievedUser.getFirstname());
        assertEquals("Doe", retrievedUser.getLastname());
    }
}