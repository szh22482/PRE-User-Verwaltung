package at.spengergasse.backend.persistence;

import at.spengergasse.backend.model.ERoles;
import at.spengergasse.backend.model.Role;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface RoleRepository extends Repository<Role, Long> {
    Role save(Role role);
    List<Role> findAll();
    Role findById(Long id);
    Role findByRole(ERoles roleName);
}
