package at.spengergasse.backend.persistence;

import at.spengergasse.backend.model.User;
import at.spengergasse.backend.model.UserToRoles;
import at.spengergasse.backend.model.UserToRolesId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserToRolesRepository extends JpaRepository<UserToRoles, UserToRolesId> {
    @Modifying
    @Transactional
    @Query("DELETE FROM UserToRoles ur WHERE ur.user = :user")
    void deleteByUser(@Param("user") User user);
    List<UserToRoles> findByUser(User user);
}
