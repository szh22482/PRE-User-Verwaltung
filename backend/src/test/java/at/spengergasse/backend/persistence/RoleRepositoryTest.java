package at.spengergasse.backend.persistence;

import at.spengergasse.backend.model.ERoles;
import at.spengergasse.backend.model.Role;
import at.spengergasse.backend.persistence.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testFindById() {
        // Suche die Rolle nach der ID
        Role retrievedRole = roleRepository.findById(1L);

        // Überprüfe, ob die gespeicherte Rolle korrekt abgerufen wurde
        assertThat(retrievedRole).isNotNull();
        assertThat(retrievedRole.getRoleName().equals(ERoles.ADMINISTRATOR));
    }

    @Test
    public void testFindByRoleName() {
        // Suche die Rolle nach dem Rollennamen
        Role retrievedRole = roleRepository.findByRoleName(ERoles.AUDITOR);

        // Überprüfe, ob die gespeicherte Rolle korrekt abgerufen wurde
        assertThat(retrievedRole).isNotNull();
        assertThat(retrievedRole.getRoleName().equals(ERoles.AUDITOR));
    }

    @Test
    public void testFindAllRoles() {
        // Suche alle Rollen
        List<Role> allRoles = roleRepository.findAll();

        // Überprüfe, ob die gespeicherten Rollen korrekt abgerufen wurden
        assertThat(allRoles).isNotNull();
        assertThat(allRoles.size() >= 2); // Annahme: Es gibt bereits 6 Rollen in der Datenbank
    }

}