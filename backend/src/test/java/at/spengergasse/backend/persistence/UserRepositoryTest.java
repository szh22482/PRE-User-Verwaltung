package at.spengergasse.backend.persistence;

import at.spengergasse.backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
                .created(LocalDate.now())
                .build();

        // Speichere den Benutzer in der Datenbank
        userRepository.save(user);

        // Suche den Benutzer nach der ID
        User retrievedUser = userRepository.findById(user.getId());

        // Überprüfe, ob der gespeicherte Benutzer korrekt abgerufen wurde
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getFirstname().equals("Max"));
        assertThat(retrievedUser.getLastname().equals("Mustermann"));
        assertThat(retrievedUser.getEmail().equals("max.mustermann@example.com"));
        assertThat(retrievedUser.getDeleted().equals(false));
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
                .created(LocalDate.now())
                .build();
        // Suche den Benutzer nach der E-Mail-Adresse
        User retrievedUser = userRepository.findByEmail("alice.doe@example.com");

        // Überprüfe, ob der gespeicherte Benutzer korrekt abgerufen wurde
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getFirstname().equals("Alice"));
        assertThat(retrievedUser.getLastname().equals("Doe"));
    }
}