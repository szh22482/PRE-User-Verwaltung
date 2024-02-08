package at.spengergasse.backend.model;

import at.spengergasse.backend.persistence.RoleRepository;
import at.spengergasse.backend.persistence.UserRepository;
import at.spengergasse.backend.persistence.UserToRolesRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class Role {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserToRolesRepository userToRolesRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void addUserToRoles() {
        List<User> users = userRepository.findAll();
        List<Role> roles = roleRepository.findAll();

        for(User user : users) {
            for(Role role : roles) {
                userToRolesRepository.save(UserToRoles.builder()
                        .id(new UserToRolesId(user.getId(), role.()))
                        .user(user)
                        .role(role)
                        .build());
            }
        }
    }
}
