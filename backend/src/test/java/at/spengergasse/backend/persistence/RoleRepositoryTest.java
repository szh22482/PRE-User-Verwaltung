package at.spengergasse.backend.persistence;

import at.spengergasse.backend.model.ERoles;
import at.spengergasse.backend.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void verifyFindById() {
        // Suche die Rolle nach der ID
        Role retrievedRole = roleRepository.findById(1L);

        // Überprüfe, ob die gespeicherte Rolle korrekt abgerufen wurde
        assertThat(retrievedRole).isNotNull();
        assertThat(retrievedRole.getRoleName().equals(ERoles.ADMINISTRATOR));
    }

    @Test
    public void verifyFindByRoleName() {
        // Suche die Rolle nach dem Rollennamen
        Role retrievedRole = roleRepository.findByRoleName(ERoles.AUDITOR);

        // Überprüfe, ob die gespeicherte Rolle korrekt abgerufen wurde
        assertThat(retrievedRole).isNotNull();
        assertThat(retrievedRole.getRoleName().equals(ERoles.AUDITOR));
    }

    @Test
    public void verifyFindAllRole() {
        // Suche alle Rollen
        List<Role> allRoles = roleRepository.findAll();

        // Überprüfe, ob die gespeicherten Rollen korrekt abgerufen wurden
        assertThat(allRoles).isNotNull();
        assertThat(!allRoles.isEmpty());
    }

}