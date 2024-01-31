package at.spengergasse.backend.persistence;

import at.spengergasse.backend.model.User;
import at.spengergasse.backend.model.UserToRoles;
import at.spengergasse.backend.model.UserToRolesId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface UserToRolesRepository extends Repository<UserToRoles, UserToRolesId> {
    void save(UserToRoles userToRoles);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserToRoles ur WHERE ur.user = :user")
    void deleteByUser(@Param("user") User user);
}
